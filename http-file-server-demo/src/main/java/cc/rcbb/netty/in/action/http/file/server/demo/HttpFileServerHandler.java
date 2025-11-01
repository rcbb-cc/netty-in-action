package cc.rcbb.netty.in.action.http.file.server.demo;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HTTP文件服务器处理器
 * 负责处理HTTP文件请求，支持文件下载和目录浏览功能
 *
 * @author rcbb.cc
 * @date 2025/11/1
 * @since 1.0.0
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * 不安全的URI模式，用于检测恶意字符
     */
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"\\[\\]].*");

    /**
     * 允许的文件名模式，只允许字母、数字、下划线、横线和点
     */
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    /**
     * 处理接收到的HTTP请求
     *
     * @param channelHandlerContext 通道处理器上下文
     * @param fullHttpRequest       完整的HTTP请求对象
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        // 检查请求解码是否成功
        if (!fullHttpRequest.decoderResult().isSuccess()) {
            sendError(channelHandlerContext, BAD_REQUEST);
            return;
        }
        // 只支持GET请求方法
        if (fullHttpRequest.method() != GET) {
            sendError(channelHandlerContext, METHOD_NOT_ALLOWED);
            return;
        }
        // 获取并清理URI路径
        String uri = fullHttpRequest.getUri();
        final String path = sanitizeUri(uri);
        if (path == null) {
            // URI不合法，返回禁止访问
            sendError(channelHandlerContext, FORBIDDEN);
            return;
        }
        // 创建文件对象并检查是否存在
        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            // 文件不存在或被隐藏，返回404
            sendError(channelHandlerContext, NOT_FOUND);
            return;
        }
        // 如果是目录，显示目录列表或重定向
        if (file.isDirectory()) {
            if (uri.endsWith("/")) {
                // 显示目录列表
                sendListing(channelHandlerContext, file);
            } else {
                // 重定向到带斜杠的URI
                sendRedirect(channelHandlerContext, uri + '/');
            }
            return;
        }
        // 确保是普通文件
        if (!file.isFile()) {
            sendError(channelHandlerContext, FORBIDDEN);
            return;
        }
        RandomAccessFile randomAccessFile = null;
        try {
            // 以只读方式打开
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (Exception e) {
            sendError(channelHandlerContext, NOT_FOUND);
            return;
        }
        // 获取文件长度并构建HTTP响应
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        // 设置Content-Length响应头
        setContentLength(response, fileLength);
        // 设置Content-Type响应头
        setContentTypeHeader(response, file);
        // 如果请求支持长连接，设置Connection响应头
        if (isKeepAlive(fullHttpRequest)) {
            response.headers().set(CONNECTION, KEEP_ALIVE);
        }
        // 写入HTTP响应头
        channelHandlerContext.write(response);
        // 使用ChunkedFile分块传输文件内容，每次传输8192字节
        ChannelFuture sendFileFuture = channelHandlerContext.write(
                new ChunkedFile(randomAccessFile, 0, fileLength, 8192),
                channelHandlerContext.newProgressivePromise());
        // 添加文件传输进度监听器
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                System.out.println("Transfer complete.");
            }

            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                if (total < 0) {
                    // 传输长度未知
                    System.err.println("Transfer progress: " + progress);
                } else {
                    // 显示传输进度
                    System.err.println("Transfer progress: " + progress + " / " + total);
                }
            }
        });
        // 发送HTTP响应的最后内容标记
        ChannelFuture lastContentFuture = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        // 如果不是长连接，传输完成后关闭连接
        if (!isKeepAlive(fullHttpRequest)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 清理并验证URI，防止路径遍历攻击
     *
     * @param uri 原始URI
     * @return 清理后的文件系统路径，如果URI不合法则返回null
     */
    private String sanitizeUri(String uri) {
        // URL解码，首先尝试UTF-8，失败则使用ISO-8859-1
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (Exception e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (Exception e1) {
                throw new Error(e1);
            }
        }
        // 将URI路径分隔符转换为系统文件分隔符
        uri = uri.replace('/', File.separatorChar);
        // 检查URI是否包含不安全的路径元素
        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.startsWith(".") || uri.endsWith(".") ||
                INSECURE_URI.matcher(uri).matches()) {
            return null;
        }
        // 返回完整的文件系统路径
        return System.getProperty("user.dir") + File.separator + uri;
    }

    /**
     * 异常处理方法
     *
     * @param ctx   通道处理器上下文
     * @param cause 异常原因
     * @throws Exception 处理异常时可能抛出的异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 发送错误响应
     *
     * @param channelHandlerContext 通道处理器上下文
     * @param status                HTTP响应状态码
     */
    private static void sendError(ChannelHandlerContext channelHandlerContext, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 判断HTTP请求是否支持长连接
     *
     * @param request HTTP请求对象
     * @return 如果支持长连接返回true，否则返回false
     */
    private static boolean isKeepAlive(FullHttpRequest request) {
        String connection = request.headers().get(CONNECTION);
        if (connection != null && connection.equalsIgnoreCase(CLOSE.toString())) {
            return false;
        }
        if (request.protocolVersion().isKeepAliveDefault()) {
            return !connection.equalsIgnoreCase(CLOSE.toString());
        } else {
            return connection.equalsIgnoreCase(KEEP_ALIVE.toString());
        }
    }

    /**
     * 根据文件扩展名设置Content-Type响应头
     *
     * @param response HTTP响应对象
     * @param file     文件对象
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        String fileName = file.getName();
        // 默认MIME类型为二进制流
        String mimeType = "application/octet-stream";

        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            mimeType = "text/html; charset=UTF-8";
        } else if (fileName.endsWith(".txt")) {
            mimeType = "text/plain; charset=UTF-8";
        } else if (fileName.endsWith(".css")) {
            mimeType = "text/css; charset=UTF-8";
        } else if (fileName.endsWith(".js")) {
            mimeType = "application/javascript; charset=UTF-8";
        } else if (fileName.endsWith(".json")) {
            mimeType = "application/json; charset=UTF-8";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            mimeType = "image/png";
        } else if (fileName.endsWith(".gif")) {
            mimeType = "image/gif";
        } else if (fileName.endsWith(".pdf")) {
            mimeType = "application/pdf";
        } else if (fileName.endsWith(".xml")) {
            mimeType = "application/xml; charset=UTF-8";
        }

        response.headers().set(CONTENT_TYPE, mimeType);
    }

    /**
     * 设置Content-Length响应头
     *
     * @param response   HTTP响应对象
     * @param fileLength 文件长度
     */
    private static void setContentLength(HttpResponse response, long fileLength) {
        response.headers().set(CONTENT_LENGTH, fileLength);
    }

    /**
     * 发送目录列表页面
     *
     * @param channelHandlerContext 通道处理器上下文
     * @param directory             目录文件对象
     */
    private static void sendListing(ChannelHandlerContext channelHandlerContext, File directory) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        // 构建HTML目录列表页面
        StringBuilder buf = new StringBuilder()
                .append("<!DOCTYPE html>\r\n")
                .append("<html><head><meta charset='utf-8' /><title>")
                .append("Listing of: ")
                .append(directory.getPath())
                .append("</title></head><body>\r\n")
                .append("<h3>Listing of: ")
                .append(directory.getPath())
                .append("</h3>\r\n")
                .append("<ul>")
                .append("<li><a href=\"../\">..</a></li>\r\n");
        // 遍历目录中的文件
        for (File f : directory.listFiles()) {
            if (f.isHidden() || !f.canRead()) {
                // 跳过隐藏文件和不可读文件
                continue;
            }
            String name = f.getName();
            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                // 跳过不符合命名规则的文件
                continue;
            }
            buf.append("<li><a href=\"")
                    .append(name)
                    .append("\">")
                    .append(name)
                    .append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 发送HTTP重定向响应
     *
     * @param ctx    通道处理器上下文
     * @param newUri 重定向的新URI
     */
    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}