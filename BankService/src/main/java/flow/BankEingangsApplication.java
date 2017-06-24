package flow;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
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
import org.springframework.integration.transaction.PseudoTransactionManager;
import org.springframework.integration.transaction.TransactionSynchronizationFactory;
import org.springframework.integration.transaction.TransactionSynchronizationProcessor;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.springframework.integration.xml.transformer.UnmarshallingTransformer;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import repositories.EinzahlungRepository;
import data.Datei;
import data.Einzahlung;
import data.JaxbBICAdapter;
import data.JaxbDateAdapter;
import data.JaxbIBANAdapter;
import data.JaxbMonetaryAmountAdapter;
import data.KontoAuszug;

@SpringBootApplication
@ComponentScan({ "repositories", "services", "data", "flow" })
@IntegrationComponentScan({ "repositories", "services", "data", "flow" })
@EnableJpaRepositories({ "repositories" })
@EntityScan("data")
public class BankEingangsApplication {

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

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow processFileFlow(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext,
            Jaxb2Marshaller einzahlungUnMarshaller,
            KontoauszugsSplitter kontoauszugsSplitter,
            InDieDatei schreibeInDieDatei,
            InDieDatenbank schreibeInDieDatenbank, MessagesAmEndeZusammenfassen messagesAmEndeZusammenfassen) {
        return IntegrationFlows
                .from(fileReadingMessageSource,
                        s -> s.poller(getPoller(taskExecutor,
                                applicationContext)))
                .channel("fileInputChannel")
                .transform(new XsltPayloadTransformer(this.xsl))
                .transform(new Result2DocumentTransformer())
                .transform(new UnmarshallingTransformer(einzahlungUnMarshaller))
                .split(kontoauszugsSplitter)
                .transform(schreibeInDieDatenbank)
                .transform(schreibeInDieDatei)
                .aggregate() // a -> a.processor(messagesAmEndeZusammenfassen))

                /*
                 * .transform(new ShowObjects()) .transform(new
                 * ZeigeFilenamen("vor dem Splitten")) .split(Datei.class, d ->
                 * d.getKontoauszug().getEinzahlungen()) .transform(new
                 * ZeigeFilenamen("Nach dem Splitten"))
                 * .route(Einzahlung.class,e -> e.sollExportiertWerden(),
                 * m->m.channelMapping(Boolean.TRUE,"inDieDateiChannel")
                 * .channelMapping(Boolean.FALSE,"inDieDatenbankChannel") )
                 * 
                 * .handle("fileProcessor", "process")
                 */
                .handle("fileProcessor", "process").get();
    }

    @Bean
    FileProcessor fileProcessor() {
        return new FileProcessor();
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

    @Bean
    PseudoTransactionManager transactionManager() {
        return new PseudoTransactionManager();
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
    KontoauszugsSplitter kontoauszugsSplitter() {
        return new KontoauszugsSplitter();
    }

    @Bean
    public InDieDatei schreibeInDieDatei() {
        return new InDieDatei(inboundOutDirectory);
    }
    
    @Autowired
    public EinzahlungRepository einzahlungRepository;

    @Bean
    public InDieDatenbank schreibeInDieDatenbank() {
        return new InDieDatenbank(einzahlungRepository);
    }

    @Bean
    MessagesAmEndeZusammenfassen messagesAmEndeZusammenfassen() {
        return new MessagesAmEndeZusammenfassen();
    }

    /*--------------------------------------------*/

    public static void main(String[] args) throws IOException,
            InterruptedException {
        SpringApplication.run(BankEingangsApplication.class, args);
    }

}
