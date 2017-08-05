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
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@SpringBootApplication
@ComponentScan({ "repositories", "services", "data", "flow" })
@IntegrationComponentScan({ "repositories", "services", "data", "flow" })
@EnableJpaRepositories({ "repositories" })
@EntityScan("data")
public class BankEingangOhneKlassen extends FilePollerWithXsltTranformFlow {

    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    
    
    public BankEingangOhneKlassen() {
        super();
    }

    @Bean
    AbschlussProcessor abschlussProcessor() {
        return new AbschlussProcessor();
    }

    @Bean
    public IntegrationFlow processFileFlow(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext,
            Jaxb2Marshaller einzahlungUnMarshaller,
            KontoauszugsSplitter kontoauszugsSplitter,
            EingangsDateiErzeugen erzeugeEingangsDatei,
            InDieDatei schreibeInDieDatei, InDieDatenbank schreibeInDieDatenbank) {
        return processFileFlowBuilder(taskExecutor, fileReadingMessageSource,
                applicationContext, einzahlungUnMarshaller)
                 .transform(erzeugeEingangsDatei)
                .split(kontoauszugsSplitter).transform(schreibeInDieDatenbank)
                .transform(schreibeInDieDatei).aggregate()
                .handle("abschlussProcessor", "process").get();
    }

    @Bean
    public EingangsDateiErzeugen erzeugeEingangsDatei() {
        return new EingangsDateiErzeugen(eingangsDateiRepository);
    }

    
    @Bean
    KontoauszugsSplitter kontoauszugsSplitter() {
        return new KontoauszugsSplitter();
    }

    @Bean
    public InDieDatei schreibeInDieDatei() {
        return new InDieDatei(inboundOutDirectory);
    }

    @Bean
    public InDieDatenbank schreibeInDieDatenbank() {
        return new InDieDatenbank();
    }

}
