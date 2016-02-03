package ir.samatco.eft.tms.xml;

import javax.xml.bind.annotation.*;

/**
 * Created by Bamdad Aghili on 2/6/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xmlplugin")
public class XmlPlugin {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String action;


    @XmlAttribute
    private String title;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private String content;
    @XmlTransient
    private String pluginValues;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPluginValues() {
        return pluginValues;
    }

    public void setPluginValues(String pluginValues) {
        this.pluginValues = pluginValues;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void replacePluginValuesInConte() {
        if (pluginValues == null || pluginValues.isEmpty()) {
            return;
        }
        String[] values = pluginValues.split(",");
        for (String value : values) {
            String[] params = value.split(":");
            String replcaeFrom = params[0];
            String replcaeTo = params[1];
            content = content.replace(replcaeFrom, replcaeTo);
        }
    }

    @Override
    public String toString() {
        String contentStr = "NULL";
        if (content != null)
            contentStr = content.substring(0, content.length() < 20 ? content.length() : 20);
        return "|xmlplugin name=" + name + ", content=" + contentStr + ", action=" + (action == null ? "NULL" : action) + ", version=" + version;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof XmlPlugin) {
//                if (getId().equals(((XmlPlugin) (obj)).getId())) {
                if (getTitle().equalsIgnoreCase(((XmlPlugin) (obj)).getTitle())) {
                    return true;
                }
            } else {
                return super.equals(obj);
            }
        } catch (Exception ex) {
            return false;
        }
        return false;

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}