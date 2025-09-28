package cc.rcbb.netty.in.action.chapter01;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * ConnectExample 类提供了异步的简历链接
 *
 * @author rcbb.cc
 * @date 2025/9/28
 * @since 1.0.0
 */
public class ConnectExample {

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    public static void connect() {
        Channel channel = CHANNEL_FROM_SOMEWHERE;
        // 异步连接
        ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1", 25));
        // 注册监听器，操作完成时收到通知
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    // 创建一个 ByteBuf 放入数据
                    ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());
                    // 将数据异步发送
                    ChannelFuture wf = channelFuture.channel().writeAndFlush(buffer);

                    // ...
                } else {
                    Throwable cause = channelFuture.cause();
                    cause.printStackTrace();
                }
            }
        });
    }

}
