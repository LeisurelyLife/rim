package cn.rt.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author ruanting
 * @date 2019/11/19
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("get msg" + channel.localAddress());
        channel.writeAndFlush("[" + channel.remoteAddress() + "]" + s + "\n");
    }

}
