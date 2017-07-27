package ausgang;

import java.io.File;

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
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.xml.transformer.MarshallingTransformer;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import data.JaxbBICAdapter;
import data.JaxbDateAdapter;
import data.JaxbIBANAdapter;
import data.JaxbMonetaryAmountAdapter;

@SpringBootApplication
@ComponentScan({ "repositories", "services", "ausgang", "data", "flow" })
@IntegrationComponentScan({ "repositories", "services", "ausgang", "data",
        "flow" })
@EnableJpaRepositories({ "repositories" })
@EntityScan({ "data", "ausgang" })
public class ÜberweisungsFlow {

    @Bean
    @Qualifier("erstelleUeberweisungenChannel")
    public MessageChannel erstelleUeberweisungenChannel() {
        return new DirectChannel();
    }

    @Value("ueberweisung.xsl")
    Resource xsl;

    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    @Bean
    public IntegrationFlow createFlow(
            MessageChannel erstelleUeberweisungenChannel,
            Jaxb2Marshaller einzahlungMarshaller)
            throws ParserConfigurationException {
        return IntegrationFlows.from(erstelleUeberweisungenChannel)
                .transform(new OffeneÜberweisungenAbfragen())
                .transform(new AusgangsdateiInEinenObjektbaumUmformen())
                .transform(new MarshallingTransformer(einzahlungMarshaller))
                .<DOMResult, Node> transform(result -> result.getNode())
                .transform(new XsltPayloadTransformer(this.xsl))
                .transform(this, "docToString")
                .transform(new ErgebnisInEineDatei(inboundOutDirectory))
                .handle(x -> System.out.println("im Handler: " + x.toString()))
                .get();

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

}