package cc.rcbb.netty.in.action.time.server.demo.nio;


/**
 * TimeClient
 *
 * @author rcbb.cc
 * @date 2025/10/22
 * @since 1.0.0
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // 默认值
            }
        }
        new Thread(new TimeClientHandler("127.0.0.1", port), "TimeClient-001").start();
    }


}