package cn.rt.common.netty.translate;

/**
 * @author ruanting
 * @date 2021/6/25
 */
public abstract class SerializerAbstract implements Serializer {
    /**
     * 获取序列化算法对象
     * @param type
     * @return
     */
    public static Serializer getSerializer(byte type) {
        return SERIALIZER_MAP.get(type);
    }
}
