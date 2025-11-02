package cc.rcbb.netty.in.action.websocket.server.demo;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

/**
 * WebSocketServerHandler
 *
 * @author rcbb.cc
 * @date 2025/11/2
 * @since 1.0.0
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    WebSocketServerHandshaker handshaker = null;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handlerHttpRequest(channelHandlerContext, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(channelHandlerContext, (WebSocketFrame) msg);
        }
    }

    /**
     * 处理 WebSocket 消息
     *
     * @param channelHandlerContext
     * @param frame
     */
    private void handlerWebSocketFrame(ChannelHandlerContext channelHandlerContext, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(channelHandlerContext.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            channelHandlerContext.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }
        String request = ((TextWebSocketFrame) frame).text();
        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(request + " , 欢迎使用Netty WebSocket服务，现在时刻：" + new java.util.Date().toString()));
    }

    /**
     * 处理 Http 消息
     *
     * @param channelHandlerContext
     * @param req
     */
    private void handlerHttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest req) {
        if (!req.decoderResult().isSuccess()
                || (!"websocket".endsWith(req.headers().get("Upgrade")))) {
            sendHttpResponse(channelHandlerContext, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channelHandlerContext.channel());
        } else {
            handshaker.handshake(channelHandlerContext.channel(), req);
        }

    }

    /**
     * 发送响应
     *
     * @param channelHandlerContext
     * @param req
     * @param res
     */
    private void sendHttpResponse(ChannelHandlerContext channelHandlerContext, FullHttpRequest req, DefaultFullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            setContentLength(res, res.content().readableBytes());
        }

        ChannelFuture f = channelHandlerContext.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}