package tests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import repositories.UeberweisungRepository;
import ausgang.Ueberweisung;
import betrag.Geld;
import data.BIC;

public class TestÜberweisungenErzeugen {
    Logger LOG = LogManager.getLogger(TestÜberweisungenErzeugen.class);
  
    private UeberweisungRepository ueberweisungRepository;

    public TestÜberweisungenErzeugen(UeberweisungRepository ueberweisungRepository) {
        this.ueberweisungRepository = ueberweisungRepository;
    }

    
    public void generate(int anzahl) throws Exception {

        List<Ueberweisung> überweisungen = new ArrayList<>();
        fill(überweisungen, anzahl);
        try {
            speichern(überweisungen);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fill(List<Ueberweisung> überweisungen, int anzahl) {
        for (int i = 0; i < anzahl; i++) {
            addOne(überweisungen, i);
        }
    }

    private void addOne(List<Ueberweisung> überweisungen, int i) {
        Ueberweisung ueberweisung = new Ueberweisung();
        ueberweisung.setAuzahlung(new Date());
        ueberweisung.setBetrag(Geld.createAmount(1000 * i));
        ueberweisung.setDebitorBIC(new BIC("DBIC" + i));
        ueberweisung.setKreditorBIC(new BIC("KBIC" + i));
        überweisungen.add(ueberweisung);
    }

    @Transactional
    protected void speichern(List<Ueberweisung> überweisungen) {
        ueberweisungRepository.save(überweisungen);
    }
}
