package cn.rt.common.netty.handler;

import cn.rt.common.netty.translate.MessageTranslator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author ruanting
 * @date 2021/6/28
 */
public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("ByteToMessageDecoder" + in.toString(Charset.defaultCharset()));
        out.add(MessageTranslator.decode(in));
    }
}
