package data;

import javax.money.MonetaryAmount;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import betrag.Geld;

@Entity
@Table(name = "EINZAHLUNG")
@SequenceGenerator(name = "EINZAHLUNG_SEQ", sequenceName = "EINZAHLUNG_SEQ")
@XmlRootElement
public class Einzahlung {
    private long id;
    private String transaktion="";
    private MonetaryAmount betrag = Geld.getNull();
    private String debitorName="";
    private String verwendungszweck="";
    private IBAN debitorIBAN= new IBAN("");
    private IBAN kreditorIBAN= new IBAN("");
    private BIC debitorBIC = new BIC("");
    private BIC kreditorBIC = new BIC("");

    public Einzahlung() {
        super();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EINZAHLUNG_SEQ")
    public Long getEinzahlungId() {
        return id;
    }
    
    public void setEinzahlungId(long id) {
        this.id = id;
    }


    public String getTransaktion() {
        return transaktion;
    }

    public void setTransaktion(String transaktion) {
        this.transaktion = transaktion;
    }

    @Convert(converter = betrag.GeldKonverter.class)
    @XmlJavaTypeAdapter(JaxbMonetaryAmountAdapter.class)
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

    public String getVerwendungszweck() {
        return verwendungszweck;
    }

    public void setVerwendungszweck(String verwendungszweck) {
        this.verwendungszweck = verwendungszweck;
    }


    @Override
    public String toString() {
        return "Einzahlung [transaktion=" + transaktion + ", betrag=" + betrag
                + ", debitorName=" + debitorName + ", verwendungszweck="
                + verwendungszweck + ", debitorIBAN=" + debitorIBAN
                + ", kreditorIBAN=" + kreditorIBAN + ", debitorBIC="
                + debitorBIC + ", kreditorBIC=" + kreditorBIC + "]";
    }

    @Convert(converter = data.JaxbIBANAdapter.class)
    @XmlJavaTypeAdapter(JaxbIBANAdapter.class)
    public IBAN getDebitorIBAN() {
        return debitorIBAN;
    }

    public void setDebitorIBAN(IBAN debitorIBAN) {
        this.debitorIBAN = debitorIBAN;
    }

    @Convert(converter = data.JaxbIBANAdapter.class)
    @XmlJavaTypeAdapter(JaxbIBANAdapter.class)
    public IBAN getKreditorIBAN() {
        return kreditorIBAN;
    }

    public void setKreditorIBAN(IBAN kreditorIBAN) {
        this.kreditorIBAN = kreditorIBAN;
    }

    @Convert(converter = data.JaxbBICAdapter.class)
    @XmlJavaTypeAdapter(JaxbBICAdapter.class)
    public BIC getDebitorBIC() {
        return debitorBIC;
    }

    public void setDebitorBIC(BIC debitorBIC) {
        this.debitorBIC = debitorBIC;
    }

    @Convert(converter = data.JaxbBICAdapter.class)
    @XmlJavaTypeAdapter(JaxbBICAdapter.class)
    public BIC getKreditorBIC() {
        return kreditorBIC;
    }

    public void setKreditorBIC(BIC kreditorBIC) {
        this.kreditorBIC = kreditorBIC;
    }
  
    @Transient
    public boolean sollExportiertWerden() {
        return "99".equals(getDebitorIBAN().toString().substring(2, 4));
    }

}
