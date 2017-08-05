package flow;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import repositories.EinzahlungRepository;
import data.Datei;
import data.Einzahlung;
import flow.NeutraleTransformation;
import fehlerManagement.Bombe;

@SpringBootApplication
@ComponentScan({ "repositories", "services", "data", "flow" })
@IntegrationComponentScan({ "repositories", "services", "data", "flow" })
@EnableJpaRepositories({ "repositories" })
@EntityScan("data")
public class BankEingangMitLambdas extends FilePollerWithXsltTranformFlow {

    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    
    @Autowired
    public EinzahlungRepository einzahlungRepository;
    
    public BankEingangMitLambdas() {
        super();
    }

    @Bean
    public IntegrationFlow processFileFlow(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext,
            Jaxb2Marshaller einzahlungUnMarshaller,
            InDieDateiAggregator inDieDatei) {
        return processFileFlowBuilder(taskExecutor, fileReadingMessageSource,
                applicationContext, einzahlungUnMarshaller)
                .channel("vorDemSplittenChannel")
                .split(Datei.class, d -> d.getKontoauszug().getEinzahlungen())
                .channel("nachDemSplittenChannel")
                .<Einzahlung, Boolean> route(
                        e -> e.sollExportiertWerden(),
                        mapping -> mapping.subFlowMapping(
                                Boolean.FALSE,
                                sf -> sf.channel("sollInDieDatenbank")
                                        .<Einzahlung, Einzahlung> transform(
                                                e -> einzahlungRepository
                                                        .save(e)))

                        .subFlowMapping(
                                Boolean.TRUE,
                                sf -> sf.channel("sollInDieDatei")
                                        .<Einzahlung, Einzahlung> transform(
                                                e -> e)
                                                .transform(new Bombe(new NeutraleTransformation()))
                                                )

                ).aggregate(a -> a.processor(inDieDatei))
                .channel("wiederZusammen")
                .handle(x -> System.out.println("im Handler: " + x.toString()))
                .get();
    }

    @Bean
    public InDieDateiAggregator inDieDatei() {
        return new InDieDateiAggregator(inboundOutDirectory);
    }
}
