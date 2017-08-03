package flow;


    
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;

public class AbschlussProcessor {
    Logger LOG = LogManager.getLogger(AbschlussProcessor.class);
    
    public void process(Message<String> msg) {
        LOG.debug("Abschluss des Prozesses ");
        LOG.debug("headers = " + msg.getHeaders());
        Object content = msg.getPayload();
        LOG.debug("payload [" + content+ "]");
    }
}
