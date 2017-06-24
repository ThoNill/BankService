package flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.transformer.AbstractPayloadTransformer;

import data.Datei;

public class ShowObjects extends AbstractPayloadTransformer<Datei, Datei> {
    Logger LOG = LogManager.getLogger(ShowObjects.class);


    @Override
    protected Datei transformPayload(Datei datei) throws Exception {
        LOG.debug("Transformation wird aufgerufen");
        LOG.debug("Datei ist: " + datei);
        if (datei != null) {
            LOG.debug("Kontoauszug ist: " + datei.getKontoauszug());
            if (datei != null && datei.getKontoauszug() != null && datei.getKontoauszug().getEinzahlungen() != null) {
                LOG.debug("Einzahlungen ist: "
                        + datei.getKontoauszug().getEinzahlungen().size());
            }
        }
        return datei;
    }

}
