package cn.rt.common.netty.handler;

import cn.rt.common.netty.message.Message;
import cn.rt.common.netty.translate.MessageTranslator;
import cn.rt.common.netty.translate.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author ruanting
 * @date 2021/6/28
 */
public class PacketEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        System.out.println("MessageToByteEncoder" + msg.getClass());
        MessageTranslator.encode(msg, SerializerAlgorithm.JSON, out);
    }
}
