package cc.rcbb.netty.in.action.pprotocol.demo.client;

import cc.rcbb.netty.in.action.pprotocol.demo.NettyMessageDecoder;
import cc.rcbb.netty.in.action.pprotocol.demo.NettyMessageEncoder;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.NettyConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * NettyClient
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public class NettyClient {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    NioEventLoopGroup group = new NioEventLoopGroup();


    public void connect(String host, int port) throws Exception {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            System.out.println("Client initializing channel");
                            ch.pipeline().addLast("decoder", new NettyMessageDecoder(1024 * 1024, 4, 4));
                            ch.pipeline().addLast("encoder", new NettyMessageEncoder());
                            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                            ch.pipeline().addLast("loginAuthReqHandler", new LoginAuthReqHandler());
                            ch.pipeline().addLast("heartBeatReqHandler", new HeartBeatReqHandler());
                            System.out.println("Client pipeline configured");
                        }
                    });

            System.out.println("Netty Client connecting to " + host + ":" + port);
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("Netty Client connected");
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        try {
                            connect(NettyConstant.REMOTE_IP, NettyConstant.PORT);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyClient().connect(NettyConstant.REMOTE_IP, NettyConstant.PORT);
    }

}
