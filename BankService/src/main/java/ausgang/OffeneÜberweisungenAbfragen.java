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
    Logger LOG = LogManager.getLogger(OffeneÜberweisungenAbfragen.class);

    @Autowired
    public UeberweisungRepository ueberweisungRepository;

    @Autowired
    public AusgangsDateiRepository ausgangsDateiRepository;

    public OffeneÜberweisungenAbfragen() {

    }

    @Override
    @Transactional
    protected Object doTransform(Message<?> message) throws Exception {
        LOG.debug("ÜberweisungenInAusgangsdatei " + message.getPayload());

        List<Ueberweisung> überweisungen = ueberweisungRepository
                .ohneDateinummer();
        if (!überweisungen.isEmpty()) {
            LOG.debug("Überweisungen gefunden ");
            
            AusgangsDatei datei = new AusgangsDatei();
            datei = ausgangsDateiRepository.save(datei);
            for (Ueberweisung u : überweisungen) {
                u = ueberweisungRepository.save(u);
                datei.add(u);

            }
            datei = ausgangsDateiRepository.save(datei);

            LOG.debug("DateiNummer= " +datei.getDateiNummer());
            
            Message newMessagemessage = MessageBuilder
                    .withPayload(new Long(datei.getDateiNummer()))
                    .setHeader("info", "Datei gefüllt").build();
            return newMessagemessage;
        }
        return message;
    }
}
