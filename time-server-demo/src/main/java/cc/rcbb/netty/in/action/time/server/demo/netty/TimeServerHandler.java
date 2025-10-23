package cc.rcbb.netty.in.action.time.server.demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * TimeServerHandler
 *
 * @author rcbb.cc
 * @date 2025/10/23
 * @since 1.0.0
 */
public class TimeServerHandler extends SimpleChannelInboundHandler<ByteBuf> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        String body = byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("The time server receive order: " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
