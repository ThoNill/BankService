package flow;


    
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;

public class AbschlussProcessor {
    Logger LOG = LogManager.getLogger(AbschlussProcessor.class);
    
    public void process(Message<String> msg) {
        Object content = msg.getPayload();
        LOG.debug("Abschluss des Prozesses [" + content+ "]");
    }
}
