package cc.rcbb.netty.in.action.msgpack.demo.server;

import cc.rcbb.netty.in.action.msgpack.demo.MsgpackDecoder;
import cc.rcbb.netty.in.action.msgpack.demo.MsgpackEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

/**
 * EchoServer 类提供了服务器的实现
 *
 * @author rcbb.cc
 * @date 2025/10/26
 * @since 1.0.0
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    // 指定所使用的 NIO 传输 Channel
                    .channel(NioServerSocketChannel.class)
                    // 使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    // 添加 EchoServerHandler 到 Channel 的 ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
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
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            // 异步的绑定服务器以接受连接
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            // 获取 Channel 的 CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
