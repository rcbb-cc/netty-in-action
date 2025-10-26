package cc.rcbb.netty.in.action.msgpack.demo.client;


import cc.rcbb.netty.in.action.msgpack.demo.pojo.UserInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * EchoClientHandler 类提供了消息的处理逻辑
 *
 * @author rcbb.cc
 * @date 2025/10/26
 * @since 1.0.0
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当被通知Channel是活跃的时候，发送一条消息
        UserInfo[] users = buildUsers();
        for (UserInfo userInfo : users) {
            ctx.writeAndFlush(userInfo);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        System.out.println("Client received: " + msg);
    }

    private UserInfo[] buildUsers() {
        UserInfo[] users = new UserInfo[100];
        for (int i = 0; i < users.length; i++) {
            users[i] = new UserInfo();
            users[i].setAge(i);
            users[i].setName("Xiao Ming " + i);
        }
        return users;
    }

}
