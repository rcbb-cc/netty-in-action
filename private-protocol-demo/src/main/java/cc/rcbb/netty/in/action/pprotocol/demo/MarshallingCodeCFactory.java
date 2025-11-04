package cc.rcbb.netty.in.action.pprotocol.demo;


import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * MarshallingCodeCFactory
 *
 * @author rcbb.cc
 * @date 2025/10/28
 * @since 1.0.0
 */
public class MarshallingCodeCFactory {

    public static Marshaller buildMarshalling() {
        // 获取序列化 MarshallerFactory
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        // 创建配置对象
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        // 设置版本号
        configuration.setVersion(5);
        // 创建并返回 Marshaller 实例
        try {
            return factory.createMarshaller(configuration);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create marshaller", e);
        }
    }

    public static Unmarshaller buildUnmarshaller() {
        // 获取序列化 MarshallerFactory
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        // 创建配置对象
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        // 设置版本号
        configuration.setVersion(5);
        // 创建并返回 Marshaller 实例
        try {
            return factory.createUnmarshaller(configuration);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create marshaller", e);
        }
    }
}
