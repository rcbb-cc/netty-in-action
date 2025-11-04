package cc.rcbb.netty.in.action.pprotocol.demo;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * MarshallingDecoder
 *
 * @author rcbb.cc
 * @date 2025/11/3
 * @since 1.0.0
 */
public class MarshallingDecoder {

    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() {
        this.unmarshaller = MarshallingCodeCFactory.buildUnmarshaller();
    }

    protected Object decode(ByteBuf in) throws Exception {
        int objectSize = in.readInt();
        if (objectSize <= 0) {
            throw new Exception("Invalid object size: " + objectSize);
        }
        // 使用自定义ByteInput适配器
        ChannelBufferByteInput input = new ChannelBufferByteInput(in, objectSize);
        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            return obj;
        } finally {
            unmarshaller.close();
        }
    }

    /**
     * Netty ByteBuf 适配器，用于 JBoss Marshalling 输入
     */
    private static class ChannelBufferByteInput implements org.jboss.marshalling.ByteInput {
        private final ByteBuf buffer;
        private final int maxSize;
        private int readCount = 0;

        ChannelBufferByteInput(ByteBuf buffer, int maxSize) {
            this.buffer = buffer;
            this.maxSize = maxSize;
        }

        @Override
        public int read() throws IOException {
            if (readCount >= maxSize) {
                return -1;
            }
            if (buffer.isReadable()) {
                readCount++;
                return buffer.readByte() & 0xFF;
            }
            return -1;
        }

        @Override
        public int read(byte[] bytes) throws IOException {
            return read(bytes, 0, bytes.length);
        }

        @Override
        public int read(byte[] bytes, int offset, int length) throws IOException {
            int available = maxSize - readCount;
            if (available <= 0) {
                return -1;
            }
            length = Math.min(length, available);
            int readableBytes = buffer.readableBytes();
            if (readableBytes <= 0) {
                return -1;
            }
            length = Math.min(length, readableBytes);
            buffer.readBytes(bytes, offset, length);
            readCount += length;
            return length;
        }

        @Override
        public long skip(long n) throws IOException {
            int available = maxSize - readCount;
            if (available <= 0) {
                return 0;
            }
            n = Math.min(n, available);
            n = Math.min(n, buffer.readableBytes());
            buffer.skipBytes((int) n);
            readCount += (int) n;
            return n;
        }

        @Override
        public int available() throws IOException {
            return Math.min(maxSize - readCount, buffer.readableBytes());
        }

        @Override
        public void close() throws IOException {
            // No-op
        }
    }

}
