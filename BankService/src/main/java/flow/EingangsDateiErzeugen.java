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

public class EingangsDateiErzeugen extends AbstractTransformer {
    private static Logger LOG = LogManager
            .getLogger(EingangsDateiErzeugen.class);
    public static String DATEINR = "dateinr";

  
    public EingangsDateiRepository eingangsDateiRepository;

    public EingangsDateiErzeugen(EingangsDateiRepository eingangsDateiRepository) {
        this.eingangsDateiRepository = eingangsDateiRepository;
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        String dateiname = (String)message.getHeaders().get(DateinameInDenHeader.DATEINAME);
        LOG.debug("Lege eine EingangsDatei an " + dateiname);
        EingangsDatei datei = erzeugen(dateiname);
        return ergänzeMessageUmDateiNummer(datei, message);
    }

    @Transactional
    public EingangsDatei erzeugen(String dateiname) {
        EingangsDatei datei = new EingangsDatei();
        datei.setDateiname(dateiname);
        datei = eingangsDateiRepository.save(datei);
        LOG.debug("Anzahl der EingangsDateien: " + eingangsDateiRepository.count());
        return datei;
    }

    protected Message<?> ergänzeMessageUmDateiNummer(EingangsDatei datei,
            Message<?> message) {
        MessageBuilder<?> builder = MessageBuilder.fromMessage(message);
        builder.setHeader(DATEINR, datei.getDateiNummer());
        return builder.build();
    }

}
