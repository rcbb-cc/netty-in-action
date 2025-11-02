package cc.rcbb.netty.in.action.websocket.server.demo;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * WebSocketServer
 *
 * @author rcbb.cc
 * @date 2025/11/2
 * @since 1.0.0
 */
public class WebSocketServer {

    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 解析WebSocket握手请求（HTTP格式）
                            ch.pipeline().addLast("http—codec", new HttpServerCodec());
                            // 聚合分段的HTTP请求体
                            ch.pipeline().addLast("http—aggregator", new HttpObjectAggregator(65536));
                            // 支持大文件或数据流的传输
                            ch.pipeline().addLast("http—chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast("handler", new WebSocketServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            System.out.println("WebSocketServer started at port " + port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new WebSocketServer().bind(8080);
    }

}