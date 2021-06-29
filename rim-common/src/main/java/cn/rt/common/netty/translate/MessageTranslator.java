package cn.rt.common.netty.translate;

import cn.rt.common.netty.message.CommandType;
import cn.rt.common.netty.message.Message;
import io.netty.buffer.ByteBuf;

/**
 * @author ruanting
 * @date 2021/6/25
 */
public class MessageTranslator {
    private static final int MAGIC_NUMBER = 0x12345678;

    public static void encode(Message message, byte algorithmType, ByteBuf byteBuf) {
        // 2. 序列化 Java 对象
        byte[] bytes = SerializerAbstract.getSerializer(algorithmType).serialize(message);

        // 3. 实际编码过程
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(message.getVersion());
        byteBuf.writeByte(algorithmType);
        byteBuf.writeByte(message.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public static Message decode(ByteBuf byteBuf) {
        // 跳过 magic number
        byteBuf.skipBytes(4);
        // 跳过版本号
        byteBuf.skipBytes(1);
        // 序列化算法标识
        byte serializeAlgorithm = byteBuf.readByte();
        // 指令
        byte command = byteBuf.readByte();
        // 数据包长度
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Message> requestType = CommandType.getMessageType(command);
        Serializer serializer = SerializerAbstract.getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }
}
