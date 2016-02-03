package ir.samatco.eft.tms.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "entry")
public class Entry {
    public String getKey() {
        return key;
    }

    @XmlAttribute
    private String key;
    @XmlAttribute
    private String value;
    @XmlAttribute
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return " |Entry key=" + key + ", value=" + value;
    }
}