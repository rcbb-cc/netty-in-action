package cc.rcbb.netty.in.action.chapter01;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ConnectHandler 类提供了连接处理逻辑
 *
 * @author rcbb.cc
 * @date 2025/9/28
 * @since 1.0.0
 */
public class ConnectHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当一个新的连接已经被建立时，channelActive(ChannelHandlerContext)将会被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");
    }

}
