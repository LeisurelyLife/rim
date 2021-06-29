package cn.rt.server.netty;

import cn.rt.common.netty.handler.PacketDecoder;
import cn.rt.server.netty.handler.LoginServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author ruanting
 * @date 2019/11/19
 */
public class ServerInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        System.out.println("连接上了");
        ChannelPipeline pipeline = channel.pipeline();

//        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new PacketDecoder());
        pipeline.addLast(new LoginServerHandler());
//        pipeline.addLast(new PacketEncoder());
//        pipeline.addLast("handler", new ServerHandler());
    }

}
