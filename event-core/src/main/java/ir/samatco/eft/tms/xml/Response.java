package ir.samatco.eft.tms.xml;

import ir.samatco.eft.tms.service.Exchangeable;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bamdad Aghili on 2/6/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class Response implements Exchangeable {
    @XmlAttribute
    private String action;
    @XmlAttribute
    private String commandName;

    private String engine;

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Response() {
    }

    @XmlElement(name = "entry", type = Entry.class)
    private List<Entry> entrys = new ArrayList<>();


    @XmlElement(name = "xmlplugin", type = XmlPlugin.class)
    private List<XmlPlugin> xmlPlugins = new ArrayList<XmlPlugin>();

    @XmlElement(name = "part", type = Part.class)
    private List<Part> parts = new ArrayList<Part>();


    public List<Entry> getEntrys() {
        return entrys;
    }

    public List<XmlPlugin> getXmlPlugins() {
        return xmlPlugins;
    }

    public String getCommandName() {
        return commandName;
    }


    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        String temp = "[Response] commandName=" + commandName + " action=" + action + "  entris:{";
        for (Entry entry : entrys) {
            temp += entry.toString();
        }
        temp += "} xmlPlugin:{";
        for (XmlPlugin xmlPlugin : xmlPlugins) {
            temp += xmlPlugin.toString();
        }
        temp += "} part:{";
        for (Part part : parts) {
            temp += part.toString();
        }
        temp += "}";
        return temp;


    }
}
