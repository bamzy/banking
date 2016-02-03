package ir.samatco.eft.tms.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Bamdad Aghili on 2/6/14.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "item")
public class Item {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "|Item id=" + id + ", content=" + content;
    }
}