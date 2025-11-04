package cc.rcbb.netty.in.action.pprotocol.demo.pojo;

/**
 * NettyMessage
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public final class NettyMessage {

    private Header header;

    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Netty Message [header=" + header + "]";
    }
}
