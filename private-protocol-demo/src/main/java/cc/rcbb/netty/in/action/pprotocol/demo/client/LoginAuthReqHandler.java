package cc.rcbb.netty.in.action.pprotocol.demo.client;

import cc.rcbb.netty.in.action.pprotocol.demo.pojo.Header;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.MessageType;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * LoginAuthReqHandler
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public class LoginAuthReqHandler extends SimpleChannelInboundHandler<NettyMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyMessage message = buildLoginReq();
        ctx.writeAndFlush(message);
        System.out.println("Send login request to server : " + message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        if (msg.getHeader() != null
                && msg.getHeader().getType() == MessageType.LOGIN_RESP.getValue()) {
            byte loginResult = (byte) msg.getBody();
            if (loginResult != (byte) 0) {
                ctx.close();
            } else {
                System.out.println("Login is ok :" + msg);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.getValue());
        message.setHeader(header);
        return message;
    }

}
