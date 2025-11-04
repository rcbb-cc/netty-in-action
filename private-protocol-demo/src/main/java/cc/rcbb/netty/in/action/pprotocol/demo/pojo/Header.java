package cc.rcbb.netty.in.action.pprotocol.demo.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Header
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public final class Header {

    /**
     * 校验码，由三部分组成
     * 1.0xabef，固定值，表明该消息是私有协议，2个字节。
     * 2.主版本号，1-255，1个字节。
     * 3.次版本号，1-255，1个字节。
     */
    private int crcCode = 0xabef0101;

    private int length;

    private long sessionID;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 优先级
     */
    private byte priority;

    /**
     * 附件
     */
    private Map<String, Object> attachment = new HashMap<>();

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Header [crcCode = " + crcCode + ", length = " + length + ", sessionID = " + sessionID + ", type = " + type + ", priority = " + priority + ", attachment = " + attachment + "]";
    }
}
