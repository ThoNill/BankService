package data;

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
@Table(name = "EINGANGSDATEI")
@SequenceGenerator(name = "EINGANGSDATEI_SEQ", sequenceName = "EINGANGSDATEI_SEQ")
public class EingangsDatei {
    
    private Long dateiNummer;
    private String dateiname = null;
 
    @Basic
    @Id
    @Column(name = "DATEINUMMER")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EINGANGSDATEI_SEQ")
    public Long getDateiNummer() {
        return dateiNummer;
    }
    
    public void setDateiNummer(Long dateiNummer) {
        this.dateiNummer = dateiNummer;
    }

    private List<Einzahlung> Einzahlungen = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,mappedBy = "datei")
    public List<Einzahlung> getEinzahlungen() {
        return Einzahlungen;
    }
    public void setEinzahlungen(List<Einzahlung> Einzahlungen) {
        this.Einzahlungen = Einzahlungen;
    }

    public boolean add(Einzahlung e) {
        e.setDatei(this);
        return Einzahlungen.add(e);
    }

    
    public String getDateiname() {
        return dateiname;
    }

    public void setDateiname(String dateiname) {
        this.dateiname = dateiname;
    };


}
