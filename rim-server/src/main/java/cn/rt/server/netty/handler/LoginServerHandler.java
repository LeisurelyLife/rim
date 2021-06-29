package cn.rt.server.netty.handler;

import cn.rt.common.netty.message.ResponseMessage;
import cn.rt.common.netty.message.login.LoginRequestMessage;
import cn.rt.common.netty.translate.MessageTranslator;
import cn.rt.common.netty.translate.SerializerAlgorithm;
import cn.rt.server.service.LoginService;
import cn.rt.server.service.impl.LoginServiceImpl;
import cn.rt.server.util.SessionSocketHolder;
import cn.rt.server.util.SpringBeanFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

/**
 * @author ruanting
 * @date 2021/6/25
 */
public class LoginServerHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        System.out.println(new Date() + ": 服务端读到数据 -> " + msg);
        checkLogin(ctx, msg);
    }

    private void checkLogin(ChannelHandlerContext ctx, LoginRequestMessage message) {
        LoginService service = SpringBeanFactory.getBean(LoginServiceImpl.class);
        ResponseMessage response = service.login(message);
        if (ResponseMessage.RESP_SUCCESS.equals(response.getRespResult())) {
            SessionSocketHolder.setUserChannel(message.getUserId(), ctx.channel());
        }
        // 1. 创建 ByteBuf 对象
        ByteBuf out = ByteBufAllocator.DEFAULT.ioBuffer();
        MessageTranslator.encode(response, SerializerAlgorithm.JSON, out);
        ctx.channel().writeAndFlush(out);
    }
}
