package cc.rcbb.netty.in.action.protobuf.demo.server;

import cc.rcbb.netty.in.action.protobuf.demo.SubscribeReqProto;
import cc.rcbb.netty.in.action.protobuf.demo.SubscribeRespProto;
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
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
        if ("zhangsan".equalsIgnoreCase(req.getUserName())) {
            System.out.println("Service accept client subscribe req : [" + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    private SubscribeRespProto.SubscribeResp resp(int subReqID) {
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setSubReqID(subReqID);
        builder.setRespCode(0);
        builder.setDesc("Netty Book Order Success");
        return builder.build();
    }
}
