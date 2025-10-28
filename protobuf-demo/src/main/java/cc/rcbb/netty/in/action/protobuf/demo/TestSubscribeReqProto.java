package cc.rcbb.netty.in.action.protobuf.demo;

/**
 * TestSubscribeReqProto
 *
 * @author rcbb.cc
 * @date 2025/10/27
 * @since 1.0.0
 */
public class TestSubscribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws Exception {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("Lilinfeng");
        builder.setProductName("Netty Book For Protobuf");
        builder.addAddress("NanJing YuHuaTai");
        return builder.build();
    }

    public static void main(String[] args) throws Exception {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("Before encode : " + req.toString());
        byte[] encode = encode(req);
        SubscribeReqProto.SubscribeReq req2 = decode(encode);
        System.out.println("After decode : " + req2.toString());
        System.out.println("Assert equal : " + req2.equals(req));
    }

}
