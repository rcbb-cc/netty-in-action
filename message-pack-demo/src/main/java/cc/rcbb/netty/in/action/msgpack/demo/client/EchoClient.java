package cc.rcbb.netty.in.action.msgpack.demo.client;


import cc.rcbb.netty.in.action.msgpack.demo.MsgpackDecoder;
import cc.rcbb.netty.in.action.msgpack.demo.MsgpackEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

/**
 * EchoClient 类提供了客户端的实现
 *
 * @author rcbb.cc
 * @date 2025/10/26
 * @since 1.0.0
 */
public class EchoClient {

    private final String host;

    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                            // maxFrameLength(65535): 最大帧长度，即数据包的最大大小为65535字节
                            // lengthFieldOffset(0): 长度字段偏移量，长度字段位于数据包的第0个字节位置
                            // lengthFieldLength(2): 长度字段占用的字节数，这里是2个字节
                            // lengthAdjustment(0): 长度修正值，长度字段表示的值不需要调整
                            // initialBytesToStrip(2): 跳过的初始字节数，这里跳过前2个字节(长度字段本身)
                            socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            socketChannel.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
                            // 在 ByteBuf 之前增加 2 个字节的消息长度字段
                            socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            socketChannel.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
                            ;
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        new EchoClient(host, port).start();
    }

}
