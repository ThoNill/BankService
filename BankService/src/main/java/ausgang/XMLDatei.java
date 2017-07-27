package ausgang;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ueberweisungen")
public class XMLDatei {
    private long id;
    private long dateiNummer;
    private Date datum;

    List<XMLAuftrag> aufträge = new ArrayList<>();

    public XMLDatei(long id, long dateiNummer, Date datum) {
        super();
        this.id = id;
        this.dateiNummer = dateiNummer;
        this.datum = datum;
    }
    
    public XMLDatei() {
        super();
    }

    @XmlElement(name="id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDateiNummer() {
        return dateiNummer;
    }

    public void setDateiNummer(long dateiNummer) {
        this.dateiNummer = dateiNummer;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    @XmlElement(name="auftrag")
    public List<XMLAuftrag> getAufträge() {
        return aufträge;
    }

    public void setAufträge(List<XMLAuftrag> aufträge) {
        this.aufträge = aufträge;
    }

    public boolean add(XMLAuftrag e) {
        return aufträge.add(e);
    }
    

}
