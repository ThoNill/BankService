package ausgang;

import java.util.Date;
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

public class AusgangsdateiInEinenObjektbaumUmformen extends AbstractTransformer {
    private static Logger LOG = LogManager
            .getLogger(AusgangsdateiInEinenObjektbaumUmformen.class);

    private UeberweisungRepository ueberweisungRepository;

    private AusgangsDateiRepository ausgangsDateiRepository;

    public AusgangsdateiInEinenObjektbaumUmformen(
            UeberweisungRepository ueberweisungRepository,
            AusgangsDateiRepository ausgangsDateiRepository) {
        super();
        this.ueberweisungRepository = ueberweisungRepository;
        this.ausgangsDateiRepository = ausgangsDateiRepository;
    }

    @Override
    @Transactional
    protected Object doTransform(Message<?> message) throws Exception {
        LOG.debug("Ausgabe in InDieDatenbank " + message.getPayload());
        Long dateiNummer = (Long) message.getPayload();
        LOG.debug("DateiNummer= " + dateiNummer);

        AusgangsDatei datei = ausgangsDateiRepository.findOne(dateiNummer);
        if (datei != null) {
            LOG.debug("Datei gefunden ");
            XMLDatei xmlDatei = new XMLDatei(dateiNummer, dateiNummer,
                    new Date());

            List<Ueberweisung> überweisungen = datei.getUeberweisungen();
            for (Ueberweisung u : überweisungen) {
                XMLÜberweisung xmlÜberweiung = new XMLÜberweisung();
                xmlÜberweiung.setId(u.getUeberweisungsId());
                xmlÜberweiung.setBic(u.getKreditorBIC());
                xmlÜberweiung.setIban(u.getKreditorIBAN());
                xmlÜberweiung.setName(u.getKreditorName());
                xmlÜberweiung.setBetrag(u.getBetrag());
                xmlÜberweiung.setVerwendungszweck(u.getVerwendungszweck());

                XMLAuftrag xmlAuftrag = new XMLAuftrag();
                xmlAuftrag.setId(u.getUeberweisungsId());
                xmlAuftrag.add(xmlÜberweiung);
                xmlAuftrag.setBic(u.getDebitorBIC());
                xmlAuftrag.setIban(u.getDebitorIBAN());
                xmlAuftrag.setName(u.getDebitorName());

                xmlDatei.add(xmlAuftrag);
            }
            Message newMessagemessage = MessageBuilder.withPayload(xmlDatei)
                    .setHeader("info", "Datei umgeformt").build();
            return newMessagemessage;

        }
        return message;
    }

}
