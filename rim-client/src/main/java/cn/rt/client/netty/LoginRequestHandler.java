package cn.rt.client.netty;

import cn.rt.common.netty.message.login.LoginRequestMessage;
import cn.rt.common.netty.translate.MessageTranslator;
import cn.rt.common.netty.translate.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author ruanting
 * @date 2021/6/25
 */
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    private String userId;
    private String token;

    public LoginRequestHandler(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 2. 写数据
        System.out.println("发送登录参数");
        LoginRequestMessage message = new LoginRequestMessage();
        message.setUserId(userId);
        message.setToken(token);
        ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();
        MessageTranslator.encode(message, SerializerAlgorithm.JSON, out);
        ctx.channel().writeAndFlush(out);

        ctx.pipeline().remove(this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead" + msg);
    }
}
