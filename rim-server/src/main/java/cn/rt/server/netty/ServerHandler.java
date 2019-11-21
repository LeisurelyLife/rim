package cn.rt.server.netty;

import cn.rt.server.util.SessionSocketHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("接入" + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("失联" + ctx.channel().remoteAddress());
        Channel channel = ctx.channel();
        SessionSocketHolder.moveChannel((NioSocketChannel) channel);

        super.channelInactive(ctx);
    }
}
