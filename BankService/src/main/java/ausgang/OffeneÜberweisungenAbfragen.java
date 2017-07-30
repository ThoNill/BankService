package ausgang;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

import repositories.AusgangsDateiRepository;
import repositories.UeberweisungRepository;

public class OffeneÜberweisungenAbfragen extends AbstractTransformer {
    public static final String DATEINR = "dateinr";

    private static final Logger LOG = LogManager.getLogger(OffeneÜberweisungenAbfragen.class);

    private UeberweisungRepository ueberweisungRepository;

    private AusgangsDateiRepository ausgangsDateiRepository;

    
    public OffeneÜberweisungenAbfragen(UeberweisungRepository ueberweisungRepository,
            AusgangsDateiRepository ausgangsDateiRepository) {
        super();
        this.ueberweisungRepository = ueberweisungRepository;
        this.ausgangsDateiRepository = ausgangsDateiRepository;
    }


    @Override
    @Transactional
    public Object doTransform(Message<?> message) throws Exception {
        LOG.debug("ÜberweisungenInAusgangsdatei " + message.getPayload());

        List<Ueberweisung> überweisungen = ueberweisungRepository.ohneDateinummer();
        if (!überweisungen.isEmpty()) {
            LOG.debug("Überweisungen gefunden ");

            AusgangsDatei datei = new AusgangsDatei();
            datei = ausgangsDateiRepository.save(datei);
            for (Ueberweisung u : überweisungen) {
                datei.add(u);
                u = ueberweisungRepository.save(u);

            }
            datei = ausgangsDateiRepository.save(datei);

            LOG.debug("DateiNummer= " + datei.getDateiNummer());

            Message newMessagemessage = MessageBuilder
                    .withPayload(new Long(datei.getDateiNummer()))
                    .setHeader(DATEINR, datei.getDateiNummer()).build();
            return newMessagemessage;
        }
        return message;
    }

}
