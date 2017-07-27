package ausgang;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Convert;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import data.BIC;
import data.IBAN;
import data.JaxbBICAdapter;
import data.JaxbIBANAdapter;

@XmlRootElement
public class XMLAuftrag {
    private long id;
    private Date datum;
    private String name="";
    private IBAN iban =new IBAN("");
    private BIC bic = new BIC("");
   

    List<XMLÜberweisung> überweisungen = new ArrayList<>();


    
    public XMLAuftrag() {
        super();
    }
    
    @XmlElement(name="ueberweisung")
    public List<XMLÜberweisung> getÜberweisungen() {
        return überweisungen;
    }

    public void setÜberweisungen(List<XMLÜberweisung> überweisungen) {
        this.überweisungen = überweisungen;
    }

    public boolean add(XMLÜberweisung e) {
        return überweisungen.add(e);
    }

    @XmlElement(name="id")
    public Long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Überweisung [ Name=" + name + ", Datum="
                + datum + ", IBAN=" + iban
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

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    
    
    
}
