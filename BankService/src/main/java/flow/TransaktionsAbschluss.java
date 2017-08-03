package flow;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.transaction.IntegrationResourceHolder;
import org.springframework.integration.transaction.TransactionSynchronizationProcessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.transaction.annotation.Transactional;

import data.EingangsDatei;
import repositories.EingangsDateiRepository;

public class TransaktionsAbschluss implements
        TransactionSynchronizationProcessor {
    Logger LOG = LogManager.getLogger(TransaktionsAbschluss.class);

    private File inboundProcessedDirectory;

    private File inboundFailedDirectory;

    private EingangsDateiRepository eingangsDateiRepository;

    public TransaktionsAbschluss(File inboundProcessedDirectory,
            File inboundFailedDirectory,
            EingangsDateiRepository eingangsDateiRepository) {
        super();
        this.inboundProcessedDirectory = inboundProcessedDirectory;
        this.inboundFailedDirectory = inboundFailedDirectory;
        this.eingangsDateiRepository = eingangsDateiRepository;
    }

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
        bereinigeDatenbank(holder, eingangsDateiRepository);
    }

    protected void verschiebeDatei(IntegrationResourceHolder holder,
            File directory) {
        Message<?> message = holder.getMessage();
        if (message != null) {
            LOG.debug("Message " + message);
            File file = (File) message.getPayload();
            LOG.debug("File " + file.getAbsolutePath());
            File ziel = new File(directory.getPath() + File.separator
                    + file.getName());
            file.renameTo(ziel);
            LOG.debug("verschoben nach " + ziel.getAbsolutePath());

        }
    }

    @Transactional
    protected void bereinigeDatenbank(IntegrationResourceHolder holder,
            EingangsDateiRepository eingangsDateiRepository) {
        Message<?> message = holder.getMessage();
        LOG.debug("bereinige Datenbank mit Message " + message);

        if (message != null && message.getPayload() instanceof File) {
            File file = (File) message.getPayload();
            if (file != null) {
                for( EingangsDatei d :eingangsDateiRepository.findAll()) {
                    LOG.debug("D: " + d.getDateiname());
                }
                EingangsDatei datei = eingangsDateiRepository
                        .findByDateiname(file.getPath().toString());
                if (datei != null) {
                    eingangsDateiRepository.delete(datei);
                    LOG.debug("die Eingangsdatei " + datei.getDateiNummer()
                            + " wurde entfernt");
                } else {
                    LOG.debug("finde die Eingangsdatei "
                            + file.getPath().toString() + " nicht");
                }
            } else {
                LOG.error("File ist null");
            }

        } else {
            LOG.error("Payload isst keine Datei " + message);
        }
    }
}
