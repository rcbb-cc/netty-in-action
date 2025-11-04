package cc.rcbb.netty.in.action.pprotocol.demo.server;

import cc.rcbb.netty.in.action.pprotocol.demo.pojo.Header;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.MessageType;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * HeartBeatRespHandler
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public class HeartBeatRespHandler extends SimpleChannelInboundHandler<NettyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        if (msg.getHeader() != null
                && msg.getHeader().getType() == MessageType.HEARTBEAT_REQ.getValue()) {
            System.out.println("Receive client heart beat message: " + msg);
            NettyMessage heartBeat = buildHeartBeat();
            System.out.println("Send heart beat response for the client :" + heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.getValue());
        message.setHeader(header);
        return message;
    }
}
