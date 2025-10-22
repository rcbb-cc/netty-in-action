package cc.rcbb.netty.in.action.time.server.demo.nio;


/**
 * TimeServer
 *
 * @author rcbb.cc
 * @date 2025/10/22
 * @since 1.0.0
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 默认值
            }
        }

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
    }

}