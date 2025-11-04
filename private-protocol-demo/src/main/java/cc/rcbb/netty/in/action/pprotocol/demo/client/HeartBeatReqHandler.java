package cc.rcbb.netty.in.action.pprotocol.demo.client;

import cc.rcbb.netty.in.action.pprotocol.demo.pojo.Header;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.MessageType;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * HeartBeatReqHandler
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public class HeartBeatReqHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        if (msg.getHeader() != null
                && msg.getHeader().getType() == MessageType.LOGIN_RESP.getValue()) {
            // 登录成功后，启动定期心跳任务
            heartBeat = ctx.executor().scheduleAtFixedRate(() -> {
                NettyMessage heartBeatMsg = buildHeartBeat();
                System.out.println("Send heart beat to server : " + heartBeatMsg);
                ctx.writeAndFlush(heartBeatMsg);
            }, 5000, 5000, TimeUnit.MILLISECONDS);
        } else if (msg.getHeader() != null
                && msg.getHeader().getType() == MessageType.HEARTBEAT_RESP.getValue()) {
            System.out.println("Receive server heart beat message : " + msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_REQ.getValue());
        message.setHeader(header);
        return message;
    }
}
