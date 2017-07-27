package ausgang;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "AUSGANGSDATEI")
@SequenceGenerator(name = "AUSGANGSDATEI_SEQ", sequenceName = "AUSGANGSDATEI_SEQ")
public class AusgangsDatei {
    
    private Long dateiNummer;
  //  private Date dateiErstellt = new Date();
    

    @Basic
    @Id
    @Column(name = "DATEINUMMER")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUSGANGSDATEI_SEQ")
    public Long getDateiNummer() {
        return dateiNummer;
    }
    
    public void setDateiNummer(Long dateiNummer) {
        this.dateiNummer = dateiNummer;
    }

    private List<Ueberweisung> ueberweisungen = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "datei")
    public List<Ueberweisung> getUeberweisungen() {
        return ueberweisungen;
    }
    public void setUeberweisungen(List<Ueberweisung> ueberweisungen) {
        this.ueberweisungen = ueberweisungen;
    }

    public boolean add(Ueberweisung e) {
        e.setDatei(this);
        return ueberweisungen.add(e);
    };


}
