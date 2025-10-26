package cc.rcbb.netty.in.action.msgpack.demo;

import cc.rcbb.netty.in.action.msgpack.demo.pojo.UserInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * MsgpackDecoder
 *
 * @author rcbb.cc
 * @date 2025/10/26
 * @since 1.0.0
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        final byte[] array;
        final int length = byteBuf.readableBytes();
        array = new byte[length];
        byteBuf.getBytes(byteBuf.readerIndex(), array, 0, length);
        MessagePack messagePack = new MessagePack();
        // 指定反序列化的目标类型为 UserInfo
        list.add(messagePack.read(array, UserInfo.class));
    }
}
