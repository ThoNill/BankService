package data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Datei")
public class Datei {
    
    private KontoAuszug kontoauszug;

    public Datei() {
    }

    @XmlElement(name = "kontoauszug")
    public KontoAuszug getKontoauszug() {
        return kontoauszug;
    }

    public void setKontoauszug(KontoAuszug kontoauszug) {
        this.kontoauszug = kontoauszug;
    }


}
