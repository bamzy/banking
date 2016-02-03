package ir.samatco.eft.tms.xml;

import ir.samatco.eft.tms.entity.Engine;
import ir.samatco.eft.tms.service.EngineFactory;
import ir.samatco.eft.tms.service.Exchangeable;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Bamdad Aghili on 3/29/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "rawdata")
public class RawData implements Exchangeable {
    @XmlAttribute
    private int fromByte;
    @XmlAttribute
    private int toByte;
    @XmlElement
    private ByteBuffer data;


    public RawData(Engine engine, int start, int end) throws IOException {
        start = start - 1;
        int size = end - start;
        Engine fullEngine = EngineFactory.getEngine(engine);
        byte[] dataBytes = fullEngine.getData();
        if (start + size > dataBytes.length)
            size = dataBytes.length - start;
        data = ByteBuffer.wrap(dataBytes, start, size);
        fromByte = start + 1;
        toByte = start + size;
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getFromByte() {
        return fromByte;
    }

    public void setFromByte(int fromByte) {
        this.fromByte = fromByte;
    }

    public int getToByte() {
        return toByte;
    }

    public void setToByte(int toByte) {
        this.toByte = toByte;
    }


    @Override
    public String toString() {
        return " |RawData fromByte=" + fromByte + ", toByte=" + toByte + ", data=" + data;
    }


}
