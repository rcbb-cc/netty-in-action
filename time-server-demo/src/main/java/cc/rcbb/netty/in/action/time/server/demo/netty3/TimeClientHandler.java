package cc.rcbb.netty.in.action.time.server.demo.netty3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * TimeClientHandler
 *
 * @author rcbb.cc
 * @date 2025/10/24
 * @since 1.0.0
 */
public class TimeClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int counter;

    private byte[] req;


    public TimeClientHandler() {
        System.out.println(System.getProperty("line.separator"));
        System.out.println(("QUERY TIME ORDER" + System.getProperty("line.separator")));
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("Now is : " + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("The counter: " + (++counter));
    }
}
