package ir.samatco.eft.tms.xml;

import ir.samatco.eft.tms.service.Exchangeable;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "command")
public class Command implements Exchangeable {
    @XmlAttribute
    private String action;


    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }


    @XmlElement(name = "item", type = Item.class)
    private List<Item> items = new ArrayList<Item>();


    @XmlElement(name = "set", type = Set.class)
    private List<Set> sets = new ArrayList<Set>();


    @XmlElement(name = "entry", type = Entry.class)
    private List<Entry> entrys = new ArrayList<Entry>();


    @XmlElement(name = "xmlplugin", type = XmlPlugin.class)
    private List<XmlPlugin> xmlPlugins = new ArrayList<XmlPlugin>();


    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setEntrys(List<Entry> entrys) {
        this.entrys = entrys;
    }

    public void setXmlPlugins(List<XmlPlugin> xmlPlugins) {
        this.xmlPlugins = xmlPlugins;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Entry> getEntrys() {
        return entrys;
    }

    public List<XmlPlugin> getXmlPlugins() {
        return xmlPlugins;
    }

    public Command() {

    }


    @Override
    public String toString() {
        String temp = "[Command] action=" + action + "   entris:{";
        for (Entry entry : entrys) {
            temp += entry.toString();
        }
        temp += "} items:{";
        for (Item item : items) {
            temp += item.toString();
        }
        temp += "} sets:{";
        for (Set set : sets) {
            temp += set.toString();
        }
        temp += "} XMLPlugin:{";
        for (XmlPlugin set : xmlPlugins) {
            temp += set.toString();
        }
        temp += "}";
        return temp;

    }

}