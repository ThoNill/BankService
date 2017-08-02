package ausgang;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import repositories.AusgangsDateiRepository;
import repositories.UeberweisungRepository;

public class DateiRollback  implements Runnable {
    private static Logger LOG = LogManager.getLogger(DateiRollback.class);
    
    long id;
    
    private AusgangsDateiRepository ausgangsDateiRepository;
    
    private UeberweisungRepository ueberweisungRepository;
    
    public DateiRollback(long id,UeberweisungRepository ueberweisungRepository,
            AusgangsDateiRepository ausgangsDateiRepository) {
        super();
        this.id = id;
        this.ueberweisungRepository = ueberweisungRepository;
        this.ausgangsDateiRepository = ausgangsDateiRepository;
    }

    @Override
    public void run() {
          wiederFreigeben();
    }

    @Transactional
    protected void wiederFreigeben() {
        LOG.debug("Wieder zurücksetzen von Datei " + id);
          AusgangsDatei datei = ausgangsDateiRepository.findOne(id);
          if (datei!=null) {
              LOG.debug("Datei gefunden");
              List<Ueberweisung> l = datei.getUeberweisungen();
              int anzahl = l.size();
              for(Ueberweisung u : l) {
                  u.setDatei(null);
                  ueberweisungRepository.save(u);
              }
              ausgangsDateiRepository.save(datei);
              LOG.debug("es wurden " + anzahl + " Überweisungen freigegeben");
          } else  {
              LOG.error("Folgende AusgangsDate fehlt:  " + id);
          }
    }

}

