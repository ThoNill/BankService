package ausgang;

import javax.money.MonetaryAmount;
import javax.persistence.Convert;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import betrag.Geld;
import data.BIC;
import data.IBAN;
import data.JaxbBICAdapter;
import data.JaxbIBANAdapter;
import data.JaxbMonetaryAmountAdapter;

@XmlRootElement
public class XMLÜberweisung {
    private long id;
    private MonetaryAmount betrag = Geld.getNull();
    private String name="";
    private String verwendungszweck="";
    private IBAN iban =new IBAN("");
    private BIC bic = new BIC("");
    
   
    public XMLÜberweisung() {
        super();
    }
 
    @XmlElement(name="id")
    public Long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    @Convert(converter = betrag.GeldKonverter.class)
    @XmlJavaTypeAdapter(JaxbMonetaryAmountAdapter.class)
    public MonetaryAmount getBetrag() {
        return betrag;
    }

    public void setBetrag(MonetaryAmount betrag) {
        this.betrag = betrag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerwendungszweck() {
        return verwendungszweck;
    }

    public void setVerwendungszweck(String verwendungszweck) {
        this.verwendungszweck = verwendungszweck;
    }


    @Override
    public String toString() {
        return "Überweisung [betrag=" + betrag
                + ", Name=" + name + ", verwendungszweck="
                + verwendungszweck + ", IBAN=" + iban
                + ", BIC="
                + bic + "]";
    }

    @Convert(converter = data.JaxbIBANAdapter.class)
    @XmlJavaTypeAdapter(JaxbIBANAdapter.class)
    public IBAN getIban() {
        return iban;
    }

    public void setIban(IBAN iban) {
        this.iban = iban;
    }

    @Convert(converter = data.JaxbBICAdapter.class)
    @XmlJavaTypeAdapter(JaxbBICAdapter.class)
    public BIC getBic() {
        return bic;
    }

    public void setBic(BIC bic) {
        this.bic = bic;
    }

}
