package ir.samatco.eft.tms.pos;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Created by user on 2/4/14.
 */
public class PosFrameEncoder extends OneToOneEncoder {


    private int sizeLength;


    public PosFrameEncoder(int sizeLength) {
        this.sizeLength = sizeLength;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        ChannelBuffer innerBuffer = (ChannelBuffer) msg;
        int len = innerBuffer.capacity();
        ChannelBuffer outerBuffer = ChannelBuffers.buffer(len + sizeLength);
        String strLen = Integer.toString(len);
        while (strLen.length() < sizeLength) {
            strLen = "0" + strLen;
        }
        System.out.println(" sent message size was:" + strLen + "\n");
        outerBuffer.writeBytes(strLen.getBytes());
        outerBuffer.writeBytes(innerBuffer);
        return outerBuffer;
    }
}
