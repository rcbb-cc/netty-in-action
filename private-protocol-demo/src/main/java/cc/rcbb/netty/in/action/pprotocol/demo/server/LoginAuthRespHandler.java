package cc.rcbb.netty.in.action.pprotocol.demo.server;

import cc.rcbb.netty.in.action.pprotocol.demo.pojo.Header;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.MessageType;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LoginAuthRespHandler
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public class LoginAuthRespHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    private String[] whiteList = {"127.0.0.1", "192.168.1.12"};

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        if (msg.getHeader() != null
                && msg.getHeader().getType() == MessageType.LOGIN_REQ.getValue()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            // 重复登录
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildLoginResp((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for (String WIP : whiteList) {
                    if (WIP.equals(ip)) {
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK ? buildLoginResp((byte) 0) : buildLoginResp((byte) -1);
                if (isOK) {
                    nodeCheck.put(nodeIndex, true);
                }
            }
            System.out.println("The login response is : " + loginResp + " body [" + loginResp.getBody() + "]");
            ctx.writeAndFlush(loginResp);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginResp(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.getValue());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
