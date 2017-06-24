package flow;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.transaction.IntegrationResourceHolder;
import org.springframework.integration.transaction.TransactionSynchronizationProcessor;
import org.springframework.messaging.Message;

public class TransaktionsAbschluss implements
        TransactionSynchronizationProcessor {
    Logger LOG = LogManager.getLogger(TransaktionsAbschluss.class);

    
    
    public TransaktionsAbschluss(File inboundProcessedDirectory,
            File inboundFailedDirectory) {
        super();
        this.inboundProcessedDirectory = inboundProcessedDirectory;
        this.inboundFailedDirectory = inboundFailedDirectory;
    }
    private File inboundProcessedDirectory;

    private File inboundFailedDirectory;

    @Override
    public void processBeforeCommit(IntegrationResourceHolder holder) {
    }

    @Override
    public void processAfterCommit(IntegrationResourceHolder holder) {
        verschiebeDatei(holder, inboundProcessedDirectory);
    }

    @Override
    public void processAfterRollback(IntegrationResourceHolder holder) {
        verschiebeDatei(holder, inboundFailedDirectory);
    }

    protected void verschiebeDatei(IntegrationResourceHolder holder,
            File directory) {
        Message<?> message = holder.getMessage();
        if (message != null) {
            LOG.debug("Message " + message);
            File file = (File) message.getPayload();
            LOG.debug("File " + file.getAbsolutePath());
            file.renameTo(new File(directory.getPath() + File.separator
                    + file.getName()));
            LOG.debug("verschoben nach " + file.getAbsolutePath());
            
        }
    }
}
