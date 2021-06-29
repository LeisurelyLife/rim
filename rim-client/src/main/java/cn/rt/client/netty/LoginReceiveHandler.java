package cn.rt.client.netty;

import cn.rt.client.RimClient;
import cn.rt.common.netty.message.ResponseMessage;
import cn.rt.common.netty.message.login.LoginResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author ruanting
 * @date 2021/6/28
 */
public class LoginReceiveHandler extends SimpleChannelInboundHandler<LoginResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponseMessage msg) throws Exception {
        if (ResponseMessage.RESP_SUCCESS.equals(msg.getRespResult())) {
            RimClient.userName = RimClient.cachName;
            RimClient.isLogin = true;
            System.out.println(RimClient.userName + "登录成功！");
        } else {
            System.out.println(RimClient.cachName + "用户登录失败，请重试！");
        }
    }
}
