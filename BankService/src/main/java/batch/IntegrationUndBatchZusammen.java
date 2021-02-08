package batch;

import java.io.File;

import javax.xml.transform.dom.DOMResult;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.classify.annotation.Classifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.xml.transformer.UnmarshallingTransformer;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.messaging.MessageHandler;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Document;

import fehlerManagement.Bombe;
import flow.BankEingangMitKlassen;
import flow.DateinameInDenHeader;
import flow.EingangsDateiErzeugen;
import flow.FilePollerFlow;
import flow.InDieDatei;
import flow.InDieDatenbank;
import flow.KontoauszugsSplitter;

@SpringBootApplication
@EnableBatchProcessing
@ComponentScan({ "repositories", "services", "ausgang", "data", "flow", "batch" })
@IntegrationComponentScan({ "repositories", "services", "ausgang", "data",
        "flow", "batch" })
@EnableJpaRepositories({ "repositories" })
@EntityScan({ "data", "ausgang" })
public class IntegrationUndBatchZusammen extends BankEingangMitKlassen { 
	// FilePollerFlow {
    
    @Autowired
    public JobLauncher jobLauncher;
    
    @Bean(name="myGateway")
    public MessageHandler jobLaunchingGateway() {
        return new JobLaunchingGateway(jobLauncher);
    }
    
    @Autowired
    @Qualifier("myJob")
    private Job job;

    
    @Transformer
    public JobLaunchRequest makeJobLunchRequest(File file) {
        JobParameters param = getJobParameters(file);
        return new JobLaunchRequest(job, param);
    }
    
    public JobParameters getJobParameters(File file) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("datei", file.getAbsolutePath());
        return jobParametersBuilder.toJobParameters();
    }
/*
    @Bean
    public IntegrationFlow processFileFlow(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext, 
            @Qualifier("myGateway") MessageHandler jobLaunchingGateway) {
        return processFileFlowBuilder(taskExecutor, fileReadingMessageSource,
                applicationContext).transform("makeJobLunchRequest").handle(jobLaunchingGateway).get();
    }
*/
    @Bean
    public IntegrationFlow processFileFlow(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext,
            Jaxb2Marshaller einzahlungUnMarshaller,
            KontoauszugsSplitter kontoauszugsSplitter,
            InDieDatei schreibeInDieDatei,
            EingangsDateiErzeugen erzeugeEingangsDatei,
            InDieDatenbank schreibeInDieDatenbank,
            MessageHandler jobLaunchingGateway) {
            return processFileFlowBuilder(taskExecutor, fileReadingMessageSource,
                        applicationContext).transform(name -> new File(name.toString())).
                        transform("makeJobLunchRequest").handle(jobLaunchingGateway).get();
    }
 
 
    
}
