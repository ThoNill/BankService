package flow;

import java.io.File;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.PollerSpec;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.transaction.DefaultTransactionSynchronizationFactory;
import org.springframework.integration.transaction.TransactionSynchronizationFactory;
import org.springframework.integration.transaction.TransactionSynchronizationProcessor;
import org.springframework.messaging.MessageChannel;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public class FilePollerFlow {

    @Autowired
    @Qualifier("inboundReadDirectory")
    public File inboundReadDirectory;
    @Autowired
    @Qualifier("inboundProcessedDirectory")
    public File inboundProcessedDirectory;
    @Autowired
    @Qualifier("inboundFailedDirectory")
    public File inboundFailedDirectory;

    private String filePattern;

    public FilePollerFlow(String filePattern) {
        super();
        this.filePattern = filePattern;
    }
    

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }


    public IntegrationFlowBuilder processFileFlowBuilder(
            TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext) {
        return IntegrationFlows.from(fileReadingMessageSource,
                s -> s.poller(getPoller(taskExecutor, applicationContext)))
                .channel("fileInputChannel");

    }

    @Bean
    public MessageSource<File> fileReadingMessageSource() {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(new SimplePatternFileListFilter(filePattern));

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

    
    protected PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    TransactionSynchronizationFactory transactionSynchronizationFactory(
            ApplicationContext applicationContext) {
        TransactionSynchronizationProcessor syncProcessor = new TransaktionsAbschluss(
                inboundProcessedDirectory, inboundFailedDirectory);
        return new DefaultTransactionSynchronizationFactory(syncProcessor);
    }

}