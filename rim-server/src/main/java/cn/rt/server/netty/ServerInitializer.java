package cn.rt.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author ruanting
 * @date 2019/11/19
 */
public class ServerInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        System.out.println("连接上了");
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("handler", new ServerHandler());

    }

}
