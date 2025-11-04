package cc.rcbb.netty.in.action.pprotocol.demo.server;

import cc.rcbb.netty.in.action.pprotocol.demo.NettyMessageDecoder;
import cc.rcbb.netty.in.action.pprotocol.demo.NettyMessageEncoder;
import cc.rcbb.netty.in.action.pprotocol.demo.pojo.NettyConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * NettyServer
 *
 * @author rcbb.cc
 * @date 2025/11/4
 * @since 1.0.0
 */
public class NettyServer {

    public void bind() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new NettyMessageDecoder(1024 * 1024, 4, 4));
                        ch.pipeline().addLast("encoder", new NettyMessageEncoder());
                        ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                        ch.pipeline().addLast("loginAuthRespHandler", new LoginAuthRespHandler());
                        ch.pipeline().addLast("heartBeatRespHandler", new HeartBeatRespHandler());
                    }
                });
        b.bind(NettyConstant.REMOTE_IP, NettyConstant.PORT).sync();
        System.out.println("Netty Server is ok : " + (NettyConstant.REMOTE_IP + ":" + NettyConstant.PORT));
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().bind();
    }

}
