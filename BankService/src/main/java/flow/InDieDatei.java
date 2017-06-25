package flow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;

import data.Einzahlung;

public class InDieDatei extends AbstractTransformer {
    Logger LOG = LogManager.getLogger(InDieDatei.class);
    
    private String wo;
    private File inboundOutDirectory;
    private AtomicInteger counter = new AtomicInteger();

    public InDieDatei(File inboundOutDirectory) {
        this.inboundOutDirectory = inboundOutDirectory;
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        List<Einzahlung> einzahlungen = (List<Einzahlung>) message
                .getPayload();
        Boolean inDieDatenbank = (Boolean) message.getHeaders().get(
                "inDatenbank");
        if (!inDieDatenbank) {
            LOG.debug("Ausgabe in InDieDatei " + message.getPayload());

            File directory = inboundOutDirectory;
            long zeit = new Date().getTime();
            int nummer = counter.addAndGet(1);
            String fileName = directory.toString() + File.separator
                    + "ausgabe" + zeit + "_" + nummer + ".txt";
           
            try (FileWriter writer = new FileWriter(new File(fileName))) {
                LOG.debug("Ausgabe von " + einzahlungen.size() + " Einzahlungen");
                for (Einzahlung einzahlung : einzahlungen) {
                    LOG.debug("Schreibe Einzahlung " + einzahlung);
                    String ausgabeZeile = String.format("%s;%s;%s;%S",einzahlung.getTransaktion(),einzahlung.getDebitorIBAN(),einzahlung.getKreditorIBAN(),einzahlung.getBetrag().getNumber());
                    writer.write(ausgabeZeile);
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return einzahlungen;
    }

}
