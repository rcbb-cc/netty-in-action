package cc.rcbb.netty.in.action.chapter01;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BlockingIoExample 类提供了阻塞I/O示例
 *
 * @author rcbb.cc
 * @date 2025/9/27
 * @since 1.0.0
 */
public class BlockingIoExample {

    public void server(int portNumber) throws IOException {
        // 创建一个ServerSocket，用来监听指定端口上的连接请求
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // 阻塞，等待连接
        Socket clientSocket = serverSocket.accept();
        // 创建一个BufferedReader，用来读取输入
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        String request, response;
        while ((request = in.readLine()) != null) {
            if ("Done".equals(request)) {
                break;
            }
            // 处理请求
            response = processRequest(request);
            // 响应给客户端
            out.println(response);
        }
    }

    private String processRequest(String request) {
        return "Processed: " + request;
    }

}
