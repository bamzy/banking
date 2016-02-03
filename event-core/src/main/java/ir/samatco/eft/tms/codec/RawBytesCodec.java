package ir.samatco.eft.tms.codec;

import ir.samatco.eft.tms.service.Exchangeable;
import ir.samatco.eft.tms.xml.RawData;

import java.nio.ByteBuffer;

/**
 * Created by Bamdad Aghili on 4/6/14.
 */
public class RawBytesCodec implements Codec {

    private int sizeLength;

    public RawBytesCodec(int sizeLength) {
        this.sizeLength = sizeLength;
    }

    @Override
    public Exchangeable decode(ByteBuffer input) {
        return null;
    }

    @Override
    public ByteBuffer encode(Exchangeable input) {
        RawData rawInput = (RawData) input;
        ByteBuffer rawBuffer = rawInput.getData();
        String strFrom = Integer.toString(rawInput.getFromByte());
        String strTo = Integer.toString(rawInput.getToByte());
        while (strFrom.length() < sizeLength) {
            strFrom = "0" + strFrom;
        }
        while (strTo.length() < sizeLength) {
            strTo = "0" + strTo;
        }
        ByteBuffer outputBuffer = ByteBuffer.allocate(2 * sizeLength + rawBuffer.limit());
        outputBuffer.put(strFrom.getBytes());
        outputBuffer.put(strTo.getBytes());
        outputBuffer.put(rawBuffer);
        outputBuffer.flip();
        return outputBuffer;
    }
}
