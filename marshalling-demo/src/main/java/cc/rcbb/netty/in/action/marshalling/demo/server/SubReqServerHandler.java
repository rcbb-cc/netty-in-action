package cc.rcbb.netty.in.action.marshalling.demo.server;

import cc.rcbb.netty.in.action.marshalling.demo.pojo.SubscribeReq;
import cc.rcbb.netty.in.action.marshalling.demo.pojo.SubscribeResp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * SubReqServerHandler
 *
 * @author rcbb.cc
 * @date 2025/10/28
 * @since 1.0.0
 */
public class SubReqServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server channel active: " + ctx.channel().remoteAddress());
        System.out.println("Waiting for client messages...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("=== Server channelRead0 triggered ===");
        System.out.println("Server received message type: " + msg.getClass().getName());
        System.out.println("Message content: " + msg);
        SubscribeReq req = (SubscribeReq) msg;
        if ("zhangsan".equalsIgnoreCase(req.getUserName())) {
            System.out.println("Service accept client subscribe req : [" + req.toString() + "]");
            SubscribeResp response = resp(req.getSubReqID());
            System.out.println("Sending response: " + response);
            ctx.writeAndFlush(response);
            System.out.println("Response sent successfully");
        } else {
            System.out.println("Username mismatch: " + req.getUserName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("=== Server exception caught ===");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server channelReadComplete triggered");
        ctx.flush();
    }

    private SubscribeResp resp(int subReqID) {
        SubscribeResp resp = new SubscribeResp();
        resp.setSubReqID(subReqID);
        resp.setRespCode(0);
        resp.setDesc("Netty Book Order Success");
        System.out.println("Created response for subReqID: " + subReqID);
        return resp;
    }
}
