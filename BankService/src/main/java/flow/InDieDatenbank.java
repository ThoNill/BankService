package flow;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

import repositories.EinzahlungRepository;
import data.Einzahlung;

public class InDieDatenbank extends AbstractTransformer  {
    Logger LOG = LogManager.getLogger(InDieDatenbank.class);
    
    private String wo;
    private EinzahlungRepository repository;
    
    public InDieDatenbank(EinzahlungRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        LOG.debug("Ausgabe in InDieDatei " + message.getPayload());
        
        List<Einzahlung> einzahlungen = (List<Einzahlung>) message.getPayload();
        Boolean inDieDatenbank = (Boolean) message.getHeaders().get("inDatenbank");
        if (inDieDatenbank) {
            speichern(einzahlungen);
        }
        return einzahlungen;
    }

    @Transactional
    protected void speichern(List<Einzahlung> einzahlungen) {
        for(Einzahlung e : einzahlungen) {
                repository.save(e);
        }
    }

   


}

