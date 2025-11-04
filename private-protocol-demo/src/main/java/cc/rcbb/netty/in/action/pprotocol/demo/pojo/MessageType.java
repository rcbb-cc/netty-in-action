package cc.rcbb.netty.in.action.pprotocol.demo.pojo;

/**
 * MessageType
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public enum MessageType {

    /**
     * 握手请求消息
     */
    LOGIN_REQ((byte) 3),
    /**
     * 握手应答消息
     */
    LOGIN_RESP((byte) 4),
    /**
     * 心跳请求消息
     */
    HEARTBEAT_REQ((byte) 5),
    /**
     * 心跳应答消息
     */
    HEARTBEAT_RESP((byte) 6),

    ;

    private byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

}
