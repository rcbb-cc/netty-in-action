package cc.rcbb.netty.in.action.marshalling.demo.pojo;

import java.io.Serializable;

/**
 * SubscribeResp
 *
 * @author rcbb.cc
 * @date 2025/10/28
 * @since 1.0.0
 */
public class SubscribeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    private int subReqID;

    private int respCode;

    private String desc;

    public int getSubReqID() {
        return subReqID;
    }

    public void setSubReqID(int subReqID) {
        this.subReqID = subReqID;
    }

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "SubscribeResp{" +
                "subReqID=" + subReqID +
                ", respCode=" + respCode +
                ", desc='" + desc + '\'' +
                '}';
    }
}
