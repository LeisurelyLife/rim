package cn.rt.common.netty.translate;

import java.util.HashMap;

/**
 * 用户序列化数据对象
 * @author ruanting
 * @date 2021/6/25
 */
public interface Serializer {
    HashMap<Byte, Serializer> SERIALIZER_MAP = new HashMap() {
        {
            put(SerializerAlgorithm.JSON, new JSONSerializer());
        }
    };

    /**
     * 序列化算法
     */
    byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
