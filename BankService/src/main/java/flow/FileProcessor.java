package flow;


    
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;

public class FileProcessor {
    Logger LOG = LogManager.getLogger(FileProcessor.class);
    
    public void process(Message<String> msg) {
        Object content = msg.getPayload();
        LOG.debug("Object of Class [" + content.getClass().getCanonicalName() + "] is: " + content);
    }
}
