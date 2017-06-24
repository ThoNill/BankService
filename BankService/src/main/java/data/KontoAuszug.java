package data;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
public class KontoAuszug {
    Logger LOG = LogManager.getLogger(KontoAuszug.class);


    private List<Einzahlung> einzahlungen;

    public KontoAuszug() {
    }

    @XmlElementWrapper(name="einzahlungen")
    @XmlElement(name = "einzahlung")
    public List<Einzahlung> getEinzahlungen() {
        if (einzahlungen != null) {
            for (Einzahlung z : einzahlungen) {
                LOG.debug("in getEinzahlungen: " + z.toString());
            }
        }
        return einzahlungen;
    }

    public void setEinzahlungen(List<Einzahlung> einzahlungen) {
        this.einzahlungen = einzahlungen;
    }

}
