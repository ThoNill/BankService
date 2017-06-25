package flow;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;
import repositories.EinzahlungRepository;
import data.Einzahlung;

public class InDieDatenbank extends AbstractTransformer  {
    Logger LOG = LogManager.getLogger(InDieDatenbank.class);
    
    @Autowired
    public EinzahlungRepository einzahlungRepository;

    
    public InDieDatenbank() {
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        LOG.debug("Ausgabe in InDieDatenbank " + message.getPayload());
        
        List<Einzahlung> einzahlungen = (List<Einzahlung>) message.getPayload();
        Boolean inDieDatenbank = (Boolean) message.getHeaders().get("inDatenbank");
        if (inDieDatenbank) {
            LOG.debug("Anzahl der Objekte  "+ einzahlungen.size() );
            speichern(einzahlungen);
        }
        return einzahlungen;
    }


    @Transactional
    protected void speichern(List<Einzahlung> einzahlungen) {
        einzahlungRepository.save(einzahlungen);
    }



}

