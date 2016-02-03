package ir.samatco.eft.tms.codec;

import ir.samatco.eft.tms.service.Exchangeable;

import java.nio.ByteBuffer;

/**
 * Created by Bamdad Aghili on 2/8/14.
 */
public interface Codec {
    public Exchangeable decode(ByteBuffer input);

    public ByteBuffer encode(Exchangeable input);
}
