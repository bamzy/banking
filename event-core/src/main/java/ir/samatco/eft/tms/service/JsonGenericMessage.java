package ir.samatco.eft.tms.service;

import ir.samatco.eft.message.GenericMessage;
import ir.samatco.eft.message.Message;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * User: door
 * Date: 12/28/13
 * Time: 12:15 PM
 */
public class JsonGenericMessage extends GenericMessage {
    ObjectMapper objectMapper = new ObjectMapper();
    Writer jSONMessage = new StringWriter();

    public JsonGenericMessage() {
        setProperty("permission", true);
    }

    public void setMapEntryList(List<Map.Entry<String, Object>> list) {
        for (Map.Entry<String, Object> aList : list)
            setProperty(aList.getKey(), aList.getValue());
    }

    public void setMessage(Message message) {
        for (String key : message.keyset())
            setProperty(key, message.readProperty(key));
    }

    public void setMap(Map<String, ? extends Object> map) {
        for (String key : map.keySet())
            setProperty(key, map.get(key));
    }

    public static JsonGenericMessage getStatusMessage(String status) {
        JsonGenericMessage jsonGenericMessage = new JsonGenericMessage();
        jsonGenericMessage.setProperty("status", status);
        return jsonGenericMessage;
    }

    public JsonGenericMessage setTimeStampLongAndId(Long timeStampLong, Long id) {
        this.setProperty("timeStampLong", timeStampLong);
        this.setProperty("id", id);
        return this;
    }

    public static JsonGenericMessage getDescriptionalMessage(String message) {
        JsonGenericMessage jsonGenericMessage = new JsonGenericMessage();
        jsonGenericMessage.setProperty("message", message);
        return jsonGenericMessage;
    }

    public String getJsonString() {
        try {
            objectMapper.writeValue(jSONMessage, this.properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jSONMessage.toString();
    }

    public String getJsonString(Boolean error) {
        this.setProperty("error", error);
        try {
            objectMapper.writeValue(jSONMessage, this.properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jSONMessage.toString();
    }
}
