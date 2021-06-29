package cn.rt.common.netty.message.login;

import cn.rt.common.netty.message.CommandType;
import cn.rt.common.netty.message.ResponseMessage;

/**
 * @author ruanting
 * @date 2021/6/25
 */
public class LoginResponseMessage extends ResponseMessage {

    @Override
    public Byte getCommand() {
        return CommandType.LOGIN_RESPONSE.getCommand();
    }
}
