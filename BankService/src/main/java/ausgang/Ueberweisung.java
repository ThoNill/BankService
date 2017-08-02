package ausgang;

import java.util.Date;

import javax.money.MonetaryAmount;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import betrag.Geld;
import data.BIC;
import data.IBAN;

@Entity
@Table(name = "UEBERWEISUNG")
@SequenceGenerator(name = "UEBERWEISUNG_SEQ", sequenceName = "UEBERWEISUNG_SEQ")
public class Ueberweisung {
    private Long ueberweisungsId;
    private Date auzahlung= new Date();
    private String transaktion="";
    private MonetaryAmount betrag = Geld.getNull();
    private String debitorName="";
    private String kreditorName="";
    private String verwendungszweck="";
    private IBAN debitorIBAN= new IBAN("");
    private IBAN kreditorIBAN= new IBAN("");
    private BIC debitorBIC = new BIC("");
    private BIC kreditorBIC = new BIC("");
    private Date auzahllung = new Date();


    private AusgangsDatei datei;

    public Ueberweisung() {
        super();
    }
    
    @Basic
    @Id 
    @Column(name = "UEBERWEISUNGSID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UEBERWEISUNG_SEQ")
    public Long getUeberweisungsId() {
        return ueberweisungsId;
    }
    
    public void setUeberweisungsId(Long id) {
        this.ueberweisungsId = id;
    }


    public String getTransaktion() {
        return transaktion;
    }

    public void setTransaktion(String transaktion) {
        this.transaktion = transaktion;
    }    

    @Convert(converter = betrag.GeldKonverter.class)
    public MonetaryAmount getBetrag() {
        return betrag;
    }

    public void setBetrag(MonetaryAmount betrag) {
        this.betrag = betrag;
    }

    public String getDebitorName() {
        return debitorName;
    }

    public void setDebitorName(String debitorName) {
        this.debitorName = debitorName;
    }

    public String getKreditorName() {
        return kreditorName;
    }

    public void setKreditorName(String kreditorName) {
        this.kreditorName = kreditorName;
    }

    
    public String getVerwendungszweck() {
        return verwendungszweck;
    }

    public void setVerwendungszweck(String verwendungszweck) {
        this.verwendungszweck = verwendungszweck;
    }


    @Convert(converter = data.JaxbIBANAdapter.class)
    public IBAN getDebitorIBAN() {
        return debitorIBAN;
    }

    public void setDebitorIBAN(IBAN debitorIBAN) {
        this.debitorIBAN = debitorIBAN;
    }

    @Convert(converter = data.JaxbIBANAdapter.class)
    public IBAN getKreditorIBAN() {
        return kreditorIBAN;
    }

    public void setKreditorIBAN(IBAN kreditorIBAN) {
        this.kreditorIBAN = kreditorIBAN;
    }

    @Convert(converter = data.JaxbBICAdapter.class)
    public BIC getDebitorBIC() {
        return debitorBIC;
    }

    public void setDebitorBIC(BIC debitorBIC) {
        this.debitorBIC = debitorBIC;
    }

    @Convert(converter = data.JaxbBICAdapter.class)
    public BIC getKreditorBIC() {
        return kreditorBIC;
    }

    public void setKreditorBIC(BIC kreditorBIC) {
        this.kreditorBIC = kreditorBIC;
    }

 
    public Date getAuzahlung() {
        return auzahlung;
    }

    public void setAuzahlung(Date auzahlung) {
        this.auzahlung = auzahlung;
    }
    
    
    @ManyToOne(cascade = CascadeType.ALL,optional=true)
    @JoinColumn(name = "Nummer")
    public AusgangsDatei getDatei() {
        return datei;
    }

    
    public void setDatei(AusgangsDatei datei) {
        this.datei = datei;
    }
    
    @Override
    public String toString() {
        return "Überweisung [transaktion=" + transaktion + ", betrag=" + betrag
                + ", debitorName=" + debitorName + ", verwendungszweck="
                + verwendungszweck + ", debitorIBAN=" + debitorIBAN
                + ", kreditorIBAN=" + kreditorIBAN + ", debitorBIC="
                + debitorBIC + ", kreditorBIC=" + kreditorBIC + "]";
    }
  
}
