package fehlerManagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class RollbackHandler implements MessageHandler {
    private static Logger LOG = LogManager.getLogger(RollbackHandler.class);

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        Object payload = message.getPayload();
        LOG.debug("Bearbeite: " + payload);
        
        if (payload instanceof Throwable) {
            handleException((Throwable) payload);
        } else {
            LOG.debug("Error Payload: " + payload.getClass().toString());
        }

    }

    void handleException(Throwable ex) {
        if(ex==null) {
            return;
        }
        if (ex instanceof RollbackException) {
            LOG.debug("rolle eine Exception zurück ");
            ((RollbackException) ex).rollback();
        } else {
            handleException(ex.getCause());
        }
    }
}
