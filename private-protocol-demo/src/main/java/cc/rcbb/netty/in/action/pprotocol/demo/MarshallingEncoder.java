package cc.rcbb.netty.in.action.pprotocol.demo;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * MarshallingEncoder
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public class MarshallingEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    Marshaller marshaller;

    public MarshallingEncoder() {
        marshaller = MarshallingCodeCFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws Exception {
        try {
            int lengthPos = out.writerIndex();
            // 写入长度占位符
            out.writeBytes(LENGTH_PLACEHOLDER);
            // 记录对象数据开始位置
            int objectBeginIndex = out.writerIndex();
            // 序列化对象
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            // 计算对象实际占用大小
            int objectSize = out.writerIndex() - objectBeginIndex;
            // 设置对象长度字段
            out.setInt(lengthPos, objectSize);
        } finally {
            marshaller.close();
        }
    }

    /**
     * Netty ByteBuf 适配器，用于 JBoss Marshalling 输出
     */
    private static class ChannelBufferByteOutput implements org.jboss.marshalling.ByteOutput {
        private final ByteBuf buffer;

        ChannelBufferByteOutput(ByteBuf buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(int b) throws IOException {
            buffer.writeByte(b);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            buffer.writeBytes(bytes);
        }

        @Override
        public void write(byte[] bytes, int srcIndex, int length) throws IOException {
            buffer.writeBytes(bytes, srcIndex, length);
        }

        @Override
        public void close() throws IOException {
            // No-op
        }

        @Override
        public void flush() throws IOException {
            // No-op
        }
    }
}
