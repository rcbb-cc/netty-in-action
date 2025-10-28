package cc.rcbb.netty.in.action.protobuf.demo.client;

import cc.rcbb.netty.in.action.protobuf.demo.SubscribeReqProto;
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
        // 当被通知Channel是活跃的时候，发送一条消息
        for (int i = 0; i < 10; i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReqProto.SubscribeReq subReq(int i) {
        SubscribeReqProto.SubscribeReq req = SubscribeReqProto.SubscribeReq.newBuilder()
                .setSubReqID(i)
                .setUserName("zhangsan")
                .setProductName("Netty Book For Protobuf")
                .addAddress("Nanjing Yuhang Park 1")
                .addAddress("Nanjing Yuhang Park 2")
                .build();
        return req;
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

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}
