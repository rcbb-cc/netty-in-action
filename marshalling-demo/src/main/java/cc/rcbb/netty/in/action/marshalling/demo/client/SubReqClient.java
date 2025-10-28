package cc.rcbb.netty.in.action.marshalling.demo.client;

import cc.rcbb.netty.in.action.marshalling.demo.MarshallingCodeCFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * SubReqClient 类提供了客户端的实现
 *
 * @author rcbb.cc
 * @date 2025/10/28
 * @since 1.0.0
 */
public class SubReqClient {

    private final String host;

    private final int port;

    public SubReqClient(String host, int port) {
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
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            System.out.println("Client initializing channel");
                            // 添加日志处理器查看发送的数据
                            ch.pipeline().addLast("logger", new io.netty.handler.logging.LoggingHandler(io.netty.handler.logging.LogLevel.INFO));
                            // MarshallingDecoder/Encoder 内部已经处理了长度字段，不需要额外的 LengthField 处理器
                            ch.pipeline().addLast("decoder", MarshallingCodeCFactory.buildMarshallingDecoder());
                            ch.pipeline().addLast("encoder", MarshallingCodeCFactory.buildMarshallingEncoder());
                            ch.pipeline().addLast("handler", new SubReqClientHandler());
                            System.out.println("Client pipeline configured");
                        }
                    });

            System.out.println("Marshalling Client connecting to " + host + ":" + port);
            ChannelFuture f = b.connect().sync();
            System.out.println("Marshalling Client connected");
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        new SubReqClient(host, port).start();
    }

}
