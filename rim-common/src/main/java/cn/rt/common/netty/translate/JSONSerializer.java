package cn.rt.common.netty.translate;

import com.alibaba.fastjson.JSON;

/**
 * @author ruanting
 * @date 2021/6/25
 */
public class JSONSerializer extends SerializerAbstract {
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
