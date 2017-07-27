package flow;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.transaction.PseudoTransactionManager;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@SpringBootApplication
@ComponentScan({ "repositories", "services", "data", "flow" })
@IntegrationComponentScan({ "repositories", "services", "data", "flow" })
@EnableJpaRepositories({ "repositories" })
@EntityScan("data")
public class XSLTAnwenden extends FilePollerFlow {

    @Value("kontoauszug.xsl")
    Resource xsl;

    public XSLTAnwenden() {
        super("*.xml");
    }
  
    @Bean
    public IntegrationFlow processFileFlow(TaskExecutor taskExecutor,
            MessageSource<File> fileReadingMessageSource,
            ApplicationContext applicationContext 
            ) {
        return processFileFlowBuilder(taskExecutor, fileReadingMessageSource,
                applicationContext)
                 .channel("fileInputChannel")
                .transform(new XsltPayloadTransformer(this.xsl))
                .<DOMResult,Node> transform(result -> result.getNode())
                .transform(this,"docToString")
                .handle(s -> System.out.println(s))
                .get();
    }
    
    
    public String docToString(Document doc) throws Exception {
        DOMSource source = new DOMSource(doc);
        StringResult stringResult = new StringResult();
        transform(source, stringResult);
        return stringResult.toString();
    }

    @Override
    protected PlatformTransactionManager transactionManager() {
        return new PseudoTransactionManager();
    }

    
    public void transform(Source source, Result res) throws Exception {
        TransformerFactory.newInstance().newTransformer().transform(source, res);
    }
    
    
}
