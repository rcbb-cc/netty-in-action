package cc.rcbb.netty.in.action.time.server.demo.bio2;


import cc.rcbb.netty.in.action.time.server.demo.bio.TimeServerHandler;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * TimeServer
 * 伪异步 I/O 通信
 * <p>
 * 优化：由于线程池和消息队列都是有界的，因此，无论客户端并发连接数多大，它都不会导致线程个数过于膨胀或者内存溢出
 * <p>
 * 存在的问题：
 * - 当 Socket 的输入流进行读取操作的时候会阻塞，因此，线程池中的线程会处于阻塞状态，不会被分配新的任务。
 * - 当调用 OutputStream 的 write() 方法写输出流的时候，它将会被阻塞，直到所有要发送的字节全部写入完毕或者发生异常。
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
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port: " + port);
            Socket socket = null;
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);
            while (true) {
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}