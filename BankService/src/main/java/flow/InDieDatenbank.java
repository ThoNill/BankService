package flow;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.transaction.annotation.Transactional;

import repositories.EingangsDateiRepository;
import repositories.EinzahlungRepository;
import data.EingangsDatei;
import data.Einzahlung;

public class InDieDatenbank extends AbstractTransformer {
    private static Logger LOG = LogManager.getLogger(InDieDatenbank.class);
   

    @Autowired
    public EinzahlungRepository einzahlungRepository;

    @Autowired
    public EingangsDateiRepository eingangsDateiRepository;

    public InDieDatenbank() {
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        LOG.debug("Ausgabe in InDieDatenbank " + message.getPayload());

        List<Einzahlung> einzahlungen = (List<Einzahlung>) message.getPayload();
        Boolean inDieDatenbank = (Boolean) message.getHeaders().get(
                "inDatenbank");
        if (inDieDatenbank) {
            LOG.debug("Anzahl der Objekte  " + einzahlungen.size());
            speichern(einzahlungen, message);
        }
        return einzahlungen;
    }

    @Transactional
    protected void speichern(List<Einzahlung> einzahlungen,
            Message<?> message) {
        Long id = (Long)message.getHeaders().get(EingangsDateiErzeugen.DATEINR);
        EingangsDatei datei = eingangsDateiRepository.findOne(id);
        for (Einzahlung einzahlung : einzahlungen) {
            datei.add(einzahlung);
            einzahlungRepository.save(einzahlung);
        }
        eingangsDateiRepository.save(datei);
    }

}
