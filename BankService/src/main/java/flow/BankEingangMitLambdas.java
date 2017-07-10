package flow;

import java.io.File;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.PollerSpec;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.transaction.DefaultTransactionSynchronizationFactory;
import org.springframework.integration.transaction.TransactionSynchronizationFactory;
import org.springframework.integration.transaction.TransactionSynchronizationProcessor;
import org.springframework.integration.xml.transformer.UnmarshallingTransformer;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Document;

import repositories.EinzahlungRepository;
import data.Datei;
import data.Einzahlung;
import data.JaxbBICAdapter;
import data.JaxbDateAdapter;
import data.JaxbIBANAdapter;
import data.JaxbMonetaryAmountAdapter;
import data.KontoAuszug;

import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;

@SpringBootApplication
@ComponentScan({ "repositories", "services", "data", "flow" })
@IntegrationComponentScan({ "repositories", "services", "data", "flow" })
@EnableJpaRepositories({ "repositories" })
@EntityScan("data")
public class BankEingangMitLambdas {

    @Autowired
    @Qualifier("inboundReadDirectory")
    public File inboundReadDirectory;

    @Autowired
    @Qualifier("inboundProcessedDirectory")
    public File inboundProcessedDirectory;

    @Autowired
    @Qualifier("inboundFailedDirectory")
    public File inboundFailedDirectory;

    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    @Value("kontoauszug.xsl")
    private Resource xsl;

    @Autowired
    public EinzahlungRepository einzahlungRepository;

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    // .channel("wiederZusammen")

    @Bean
    public IntegrationFlow processFileFlow(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext,
            Jaxb2Marshaller einzahlungUnMarshaller,
            InDieDateiAggregator inDieDatei) {
        return IntegrationFlows
                .from(fileReadingMessageSource,
                        s -> s.poller(getPoller(taskExecutor,
                                applicationContext)))
                .channel("fileInputChannel")
                .transform(new XsltPayloadTransformer(this.xsl))
                .<DOMResult,Node> transform(result -> result.getNode())
                .transform(new UnmarshallingTransformer(einzahlungUnMarshaller))
                .channel("vorDemSplittenChannel")
                .split(Datei.class, d -> d.getKontoauszug().getEinzahlungen())
                .channel("nachDemSplittenChannel")
                .transform(inDieDatei,"zaehlen")
                .<Einzahlung, Boolean> route(
                        e -> e.sollExportiertWerden(),
                        mapping -> mapping
                                .subFlowMapping(
                                        Boolean.FALSE,
                                        sf -> sf
                                        .channel("sollInDieDatenbank")
                                        .<Einzahlung, Einzahlung> transform(e -> einzahlungRepository.save(e) )
                                )

                                .subFlowMapping(
                                        Boolean.TRUE,
                                        sf -> sf
                                         .channel("sollInDieDatei")
                                         .aggregate(a -> a.processor(inDieDatei))
                                )

                )

                .channel("wiederZusammen")
                .handle(x -> System.out.println("im Handler: " + x.toString()))
                .get();
    }

    @Bean
    public MessageSource<File> fileReadingMessageSource() {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(new SimplePatternFileListFilter("*.xml"));

        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(inboundReadDirectory);
        source.setFilter(filters);

        return source;
    }

    public PollerSpec getPoller(TaskExecutor taskExecutor,
            ApplicationContext applicationContext) {
        return Pollers
                .fixedDelay(1000)
                .taskExecutor(taskExecutor)
                .maxMessagesPerPoll(1)
                .transactionSynchronizationFactory(
                        transactionSynchronizationFactory(applicationContext))
                .transactional(transactionManager());
    }

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Bean
    JpaTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    TransactionSynchronizationFactory transactionSynchronizationFactory(
            ApplicationContext applicationContext) {
        TransactionSynchronizationProcessor syncProcessor = new TransaktionsAbschluss(
                inboundProcessedDirectory, inboundFailedDirectory);
        return new DefaultTransactionSynchronizationFactory(syncProcessor);
    }

    @Bean
    public Jaxb2Marshaller einzahlungUnMarshaller() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Datei.class, KontoAuszug.class,
                Einzahlung.class);
        unmarshaller.setAdapters(new JaxbDateAdapter(), new JaxbBICAdapter(),
                new JaxbIBANAdapter(), new JaxbMonetaryAmountAdapter());
        return unmarshaller;
    }
    
    @Bean
    public InDieDateiAggregator inDieDatei() {
        return new InDieDateiAggregator(inboundOutDirectory);
    }
}
