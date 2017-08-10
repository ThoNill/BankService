package flow;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
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
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import repositories.EingangsDateiRepository;
import fehlerManagement.Bombe;

@SpringBootApplication
@ComponentScan({ "repositories", "services", "ausgang","data", "flow","batch" })
@IntegrationComponentScan({ "repositories", "services", "ausgang","data", "flow","batch" })
@EnableJpaRepositories({ "repositories" })
@EntityScan({"data","ausgang"})
public class BankEingangMitKlassen extends FilePollerWithXsltTranformFlow{

    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    public BankEingangMitKlassen() {
        super();
    }


    @Bean
    public IntegrationFlow processFileFlow(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext,
            Jaxb2Marshaller einzahlungUnMarshaller,
            KontoauszugsSplitter kontoauszugsSplitter,
            InDieDatei schreibeInDieDatei,
            EingangsDateiErzeugen erzeugeEingangsDatei,
            InDieDatenbank schreibeInDieDatenbank) {
            return processFileFlowBuilder(taskExecutor, fileReadingMessageSource,
                        applicationContext, einzahlungUnMarshaller)
                        .transform(erzeugeEingangsDatei)
                .split(kontoauszugsSplitter).transform(schreibeInDieDatenbank)
                .transform(new Bombe(schreibeInDieDatei)).aggregate()
                .handle("abschlussProcessor", "process").get();
    }
    
 
    @Bean
    AbschlussProcessor abschlussProcessor() {
        return new AbschlussProcessor();
    }

    @Bean
    KontoauszugsSplitter kontoauszugsSplitter() {
        return new KontoauszugsSplitter();
    }


    @Bean
    public EingangsDateiErzeugen erzeugeEingangsDatei() {
        return new EingangsDateiErzeugen(eingangsDateiRepository);
    }

    
    @Bean
    public InDieDatei schreibeInDieDatei() {
        return new InDieDatei(inboundOutDirectory);
    }

    
   
    @Bean
    public InDieDatenbank schreibeInDieDatenbank() {
        return new InDieDatenbank();
    }

    /*--------------------------------------------*/

    public static void main(String[] args) throws IOException,
            InterruptedException {
        SpringApplication.run(BankEingangMitKlassen.class, args);
    }

}
