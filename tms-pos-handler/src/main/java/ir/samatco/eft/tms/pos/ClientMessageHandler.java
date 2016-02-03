package ir.samatco.eft.tms.pos;

import ir.samatco.eft.tms.service.ProjectRepository;
import ir.samatco.eft.tms.service.ReturnResponse;
import org.apache.logging.log4j.LogManager;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.nio.ByteBuffer;
import java.nio.channels.Channel;


/**
 * Date: 2/3/14 3:47 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class ClientMessageHandler extends SimpleChannelHandler { // (1)
    private final ProjectRepository projectRepository;

    public ClientMessageHandler(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        LogManager.getLogger(this.getClass()).info("messageReceived" + e.getMessage());
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        ByteBuffer bbuf = buf.toByteBuffer();
        Channel channel = ctx.getChannel();
        System.out.print("A message from " + channel.getRemoteAddress() + " received. \n" + new String(bbuf.array()));
        ReturnResponse answer;
        try {
            answer = projectRepository.getResponse(bbuf, channel.getId());
            if (answer == null) {
                channel.close().syncUninterruptibly();
                return;
            }
            ChannelBuffer message = ChannelBuffers.wrappedBuffer(answer.getByteBuffer());
            ChannelFuture write = channel.write(message);
            write.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
//                if(future.isSuccess())
//                    System.out.println("Send finished successfully\n");

                }
            });
            write.syncUninterruptibly();
            String responseText;
            if (message.array().length > 200) {
                responseText = new String(message.array(), 0, 200) + "...";
            } else {
                responseText = new String(message.array());
            }
            System.out.println("Response sent to: " + ctx.getChannel().getRemoteAddress().toString() + " Was:" + responseText + "\n\n");
            if (answer.isError()) {
                channel.close().syncUninterruptibly();
                return;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            channel.close().syncUninterruptibly();
            return;
        }


//
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LogManager.getLogger(this.getClass()).info("Remote Client with IP:" + ctx.getChannel().getRemoteAddress().toString() + " connected");
        super.channelConnected(ctx, e);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LogManager.getLogger(this.getClass()).info("Exception Caught with remote client: " + ctx.getChannel().getRemoteAddress().toString() + "\n Reason:" + e.getCause());
        e.getCause().printStackTrace();
        Channel ch = e.getChannel();
        ch.close();
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("Channel closed\n");
        projectRepository.deleteServicer(ctx.getChannel().getId());
        LogManager.getLogger(this.getClass()).info("Channel Closed with remote client: " + ctx.getChannel().getRemoteAddress().toString());
    }


}