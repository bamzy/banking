package ir.samatco.eft.tms.xml;

import ir.samatco.eft.tms.service.Exchangeable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Bamdad Aghili on 4/5/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "part")
public class Part implements Exchangeable {
    @XmlAttribute
    int from;
    @XmlAttribute
    int to;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return " |Part from=" + from + ", to=" + to;
    }
}
