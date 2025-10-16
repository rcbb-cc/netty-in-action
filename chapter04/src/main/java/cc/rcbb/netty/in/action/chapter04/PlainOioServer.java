package cc.rcbb.netty.in.action.chapter04;


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * PlainOioServer 未使用Netty的阻塞网络编程
 *
 * @author rcbb.cc
 * @date 2025/10/14
 * @since 1.0.0
 */
public class PlainOioServer {

    public void serve(int port) throws IOException {
        // 将服务器绑定到指定端口
        final ServerSocket socket = new ServerSocket(port);
        for (; ; ) {
            // 接受连接
            final Socket clientSocket = socket.accept();
            System.out.println("Accepted connection from " + clientSocket);
            // 创建一个线程，处理连接
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OutputStream out;
                    try {
                        // 向客户端发送数据
                        out = clientSocket.getOutputStream();
                        out.write("Hi!\r\n".getBytes(StandardCharsets.UTF_8));
                        out.flush();
                        // 关闭连接
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

}
