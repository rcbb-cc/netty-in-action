package cc.rcbb.netty.in.action.marshalling.demo.server;

import cc.rcbb.netty.in.action.marshalling.demo.MarshallingCodeCFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * SubReqServer
 *
 * @author rcbb.cc
 * @date 2025/10/28
 * @since 1.0.0
 */
public class SubReqServer {

    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("Server initializing channel: " + ch.remoteAddress());
                            // 添加原始数据监听器
                            ch.pipeline().addLast("logger", new io.netty.handler.logging.LoggingHandler(io.netty.handler.logging.LogLevel.INFO));
                            // MarshallingDecoder/Encoder 内部已经处理了长度字段，不需要额外的 LengthField 处理器
                            ch.pipeline().addLast("decoder", MarshallingCodeCFactory.buildMarshallingDecoder());
                            ch.pipeline().addLast("encoder", MarshallingCodeCFactory.buildMarshallingEncoder());
                            ch.pipeline().addLast("handler", new SubReqServerHandler());
                            System.out.println("Server pipeline configured");
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            System.out.println("Marshalling Server started on port: " + port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new SubReqServer().bind(8080);
    }

}
