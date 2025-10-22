package cc.rcbb.netty.in.action.time.server.demo.aio;


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
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "AIO-AsyncTimeServerHandler-001").start();
    }


}