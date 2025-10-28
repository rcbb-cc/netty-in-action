package cc.rcbb.netty.in.action.marshalling.demo.client;

import cc.rcbb.netty.in.action.marshalling.demo.pojo.SubscribeReq;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * SubReqClientHandler
 *
 * @author rcbb.cc
 * @date 2025/10/28
 * @since 1.0.0
 */
public class SubReqClientHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client channel active, sending requests...");
        // 当被通知Channel是活跃的时候，发送一条消息
        for (int i = 0; i < 10; i++) {
            SubscribeReq req = subReq(i);
            System.out.println("Client sending request [" + i + "]: " + req);
            ctx.write(req);
        }
        ctx.flush();
        System.out.println("Client sent 10 requests");
    }

    private SubscribeReq subReq(int i) {
        SubscribeReq req = new SubscribeReq();
        req.setSubReqID(i);
        req.setUserName("zhangsan");
        req.setProductName("Netty Book For Protobuf");
        req.setAddress("Nanjing Yuhang Park 1");
        return req;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("Client exception caught:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        System.out.println("=== Client channelRead0 triggered ===");
        System.out.println("Client received message type: " + msg.getClass().getName());
        System.out.println("Client received: " + msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client channelReadComplete triggered");
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client channel inactive");
    }

}
