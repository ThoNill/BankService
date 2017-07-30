package ausgang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.transformer.Transformer;
import org.springframework.integration.xml.transformer.MarshallingTransformer;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import repositories.AusgangsDateiRepository;
import repositories.UeberweisungRepository;
import data.JaxbBICAdapter;
import data.JaxbDateAdapter;
import data.JaxbIBANAdapter;
import data.JaxbMonetaryAmountAdapter;
import fehlerManagement.Bombe;
import fehlerManagement.RollbackEnabledTransformation;
import fehlerManagement.RollbackHandler;

@SpringBootApplication
@ComponentScan({ "repositories", "services", "ausgang", "data", "flow","fehlerManagement"  })
@IntegrationComponentScan({ "repositories", "services", "ausgang", "data",
        "flow","fehlerManagement" })
@EnableJpaRepositories({ "repositories" })
@EntityScan({ "data", "ausgang" })
public class ÜberweisungsFlow {

    @Bean
    @Qualifier("erstelleUeberweisungenChannel")
    public MessageChannel erstelleUeberweisungenChannel() {
        return new DirectChannel();
    }
    
    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    @Autowired
    MessageChannel errorChannel;


    @Autowired
    public UeberweisungRepository ueberweisungRepository;

    @Autowired
    public AusgangsDateiRepository ausgangsDateiRepository;

    
    @Bean
    public IntegrationFlow createFlow(
            MessageChannel erstelleUeberweisungenChannel,
            Jaxb2Marshaller einzahlungMarshaller,
            UeberweisungRepository ueberweisungRepository,
            AusgangsDateiRepository ausgangsDateiRepository,
            Resource xslu)
            throws ParserConfigurationException {
        return IntegrationFlows
                .from(erstelleUeberweisungenChannel)
                .transform(new Bombe(new OffeneÜberweisungenAbfragen(ueberweisungRepository,ausgangsDateiRepository)))
                .transform(modify(new AusgangsdateiInEinenObjektbaumUmformen(ueberweisungRepository,ausgangsDateiRepository),ueberweisungRepository,ausgangsDateiRepository))
                .transform(modify(new MarshallingTransformer(einzahlungMarshaller),ueberweisungRepository,ausgangsDateiRepository))
                .<DOMResult, Node> transform(result -> result.getNode())
                .transform(new XsltPayloadTransformer(xslu))
            //    .transform(modify(new XsltPayloadTransformer(xslu),ueberweisungRepository,ausgangsDateiRepository))
                .transform(this, "docToString")
                .transform(modify(new ErgebnisInEineDatei(inboundOutDirectory),ueberweisungRepository,ausgangsDateiRepository))
                .handle(x -> System.out
                        .println("die Daten wurden exportiert nach: "
                                + x.toString())).get();

    }

    public String docToString(Document doc) throws Exception {
        DOMSource source = new DOMSource(doc);
        StringResult stringResult = new StringResult();
        transform(source, stringResult);
        return stringResult.toString();
    }

    public void transform(Source source, Result res) throws Exception {
        TransformerFactory.newInstance().newTransformer()
                .transform(source, res);
    }

    @Bean
    public Jaxb2Marshaller einzahlungMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XMLDatei.class, XMLAuftrag.class,
                XMLÜberweisung.class);
        marshaller.setAdapters(new JaxbDateAdapter(), new JaxbBICAdapter(),
                new JaxbIBANAdapter(), new JaxbMonetaryAmountAdapter());
        return marshaller;
    }

    @Bean
    public IntegrationFlow errorFlow(MessageChannel errorChannel)
            throws ParserConfigurationException {
        return IntegrationFlows.from(errorChannel)
                .handle(new RollbackHandler()).get();

    }

    public Transformer modify(Transformer t,UeberweisungRepository ueberweisungRepository,
            AusgangsDateiRepository ausgangsDateiRepository) {
        return new RollbackEnabledTransformation(new Bombe(t),new DateiRollbackFabric(ueberweisungRepository,ausgangsDateiRepository));
    }
    
    @Bean
    public Resource xlsu() {
        return new ClassPathResource("ueberweisung.xsl");       
    }

    
    /* Das geht nicht mehr
    @Autowired
    @Value("ueberweisung.xsl")
    Resource xslu;
     */
    
}