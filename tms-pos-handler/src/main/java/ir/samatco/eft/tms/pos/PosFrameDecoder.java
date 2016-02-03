package ir.samatco.eft.tms.pos;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Created by user on 2/4/14.
 */
public class PosFrameDecoder extends FrameDecoder {
    final boolean discardLen;
    private int lengthOfLength = 0;

    public PosFrameDecoder(int lengthOfLength, boolean discardLen) {
        this.discardLen = discardLen;
        this.lengthOfLength = lengthOfLength;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        //enough bytes for length?
        if (buffer.readableBytes() < lengthOfLength) {
            return null;
        }

        final int baseIndex = buffer.readerIndex();
        final byte[] lenBytes = new byte[lengthOfLength];
        buffer.getBytes(baseIndex, lenBytes, 0, lengthOfLength);
//        XStream xStream = new XStream(new DomDriver());
//        xStream.registerConverter(new CommandConverter());
//        xStream.alias("command", Command.class);
        final int length = Integer.parseInt(new String(lenBytes));
//        System.out.println("LEN:" + length + "........ Readables:" + (buffer.readableBytes() - lengthOfLength));
        //enough bytes for message?
        if (buffer.readableBytes() < length + lengthOfLength) {
            return null;
        }

        final int readerIndex, finalLength;
        if (discardLen) {
            readerIndex = baseIndex + lengthOfLength;
            finalLength = length;
        } else {
            readerIndex = baseIndex;
            finalLength = lengthOfLength + length;
        }
        ChannelBuffer frame = extractFrame(buffer, readerIndex, finalLength);
        buffer.readerIndex(readerIndex + finalLength);
        return frame;
    }
}
