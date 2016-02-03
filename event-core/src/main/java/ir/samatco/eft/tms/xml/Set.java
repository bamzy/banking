package ir.samatco.eft.tms.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Bamdad Aghili on 2/15/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "set")
public class Set {
    @XmlAttribute
    private String key;
    @XmlAttribute
    private String value;
    @XmlAttribute
    private String dir;

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "|Set key=" + key + ", value=" + value + ", dir=" + dir;
    }

}
