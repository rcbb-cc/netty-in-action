package cc.rcbb.netty.in.action.msgpack.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * MsgpackEncoder
 *
 * @author rcbb.cc
 * @date 2025/10/26
 * @since 1.0.0
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MessagePack messagePack = new MessagePack();
        byte[] raw = messagePack.write(o);
        byteBuf.writeBytes(raw);
    }
}
