package cn.rt.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author ruanting
 * @date 2019/11/19
 */
public class ServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                //11 秒没有向客户端发送消息就发生心跳
                .addLast(new IdleStateHandler(11, 0, 0))
                .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                .addLast(new StringDecoder())
                .addLast(new StringEncoder())
                .addLast(new ServerHandler());
    }

}
