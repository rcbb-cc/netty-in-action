package cc.rcbb.netty.in.action.marshalling.demo;


import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * MarshallingCodeCFactory
 *
 * @author rcbb.cc
 * @date 2025/10/28
 * @since 1.0.0
 */
public class MarshallingCodeCFactory {

    /**
     * 创建JBoss Marshalling解码器（用于接收数据）
     *
     * @return MarshallingDecoder
     */
    public static MarshallingDecoder buildMarshallingDecoder() {
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory, configuration);
        // 单个消息最大长度限制设置为1MB
        MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024 * 1024);
        return decoder;
    }

    /**
     * 创建JBoss Marshalling编码器（用于发送数据）
     *
     * @return MarshallingEncoder
     */
    public static MarshallingEncoder buildMarshallingEncoder() {
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider defaultMarshallerProvider = new DefaultMarshallerProvider(factory, configuration);
        MarshallingEncoder encoder = new MarshallingEncoder(defaultMarshallerProvider);
        return encoder;
    }

}
