package flow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.annotation.Aggregator;

import data.Einzahlung;

public class InDieDateiAggregator {
    Logger LOG = LogManager.getLogger(InDieDateiAggregator.class);

    private File inboundOutDirectory;
    private AtomicInteger counter = new AtomicInteger();

    public InDieDateiAggregator(File inboundOutDirectory) {
        this.inboundOutDirectory = inboundOutDirectory;
    }

    @Aggregator
    public File schreibeInDatei(List<Einzahlung> einzahlungen) {

        File directory = inboundOutDirectory;
        long zeit = new Date().getTime();
        int nummer = counter.addAndGet(1);
        String fileName = directory.toString() + File.separator + "ausgabe"
                + zeit + "_" + nummer + ".txt";
        File file = new File(fileName);
        try (FileWriter writer = new FileWriter(file)) {
            LOG.debug("Ausgabe von " + einzahlungen.size() + " Einzahlungen");
            for (Einzahlung einzahlung : einzahlungen) {
                if (einzahlung.sollExportiertWerden()) {
                    LOG.debug("Schreibe Einzahlung " + einzahlung);
                    String ausgabeZeile = String.format("%s;%s;%s;%S",
                            einzahlung.getTransaktion(), einzahlung
                                    .getDebitorIBAN(), einzahlung
                                    .getKreditorIBAN(), einzahlung.getBetrag()
                                    .getNumber());
                    writer.write(ausgabeZeile);
                }
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

}
