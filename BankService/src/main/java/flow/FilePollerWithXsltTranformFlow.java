package flow;

import java.io.File;
import java.util.HashMap;

import javax.xml.transform.dom.DOMResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.springframework.integration.xml.transformer.UnmarshallingTransformer;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Document;

import data.Datei;
import data.Einzahlung;
import data.JaxbBICAdapter;
import data.JaxbDateAdapter;
import data.JaxbIBANAdapter;
import data.JaxbMonetaryAmountAdapter;
import data.KontoAuszug;

public class FilePollerWithXsltTranformFlow extends FilePollerFlow {

    @Value("kontoauszug.xsl")
    Resource xsl;

    public FilePollerWithXsltTranformFlow() {
        super("*.xml");
    }

    @Transformer
    protected Document transformPayload(DOMResult result) {
        return (Document) result.getNode();
    }

    public IntegrationFlowBuilder processFileFlowBuilder(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext,
            Jaxb2Marshaller einzahlungUnMarshaller
            ) {
        return processFileFlowBuilder(taskExecutor, fileReadingMessageSource,
                applicationContext)
                .transform(new DateinameInDenHeader())
                .transform(new XsltPayloadTransformer(this.xsl))
                .<DOMResult, Document> transform(x -> transformPayload(x))
                // .<DOMResult,Node> transform(result -> result.getNode())
                .transform(new UnmarshallingTransformer(einzahlungUnMarshaller));
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

}