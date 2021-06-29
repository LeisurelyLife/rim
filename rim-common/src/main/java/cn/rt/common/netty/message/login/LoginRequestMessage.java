package cn.rt.common.netty.message.login;

import cn.rt.common.netty.message.Message;
import cn.rt.common.netty.message.CommandType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ruanting
 * @date 2021/6/25
 */
@Getter
@Setter
@ToString
public class LoginRequestMessage extends Message {
    /**
     * 用户id
     */
    String userId;

    /**
     * token
     */
    String token;

    @Override
    public Byte getCommand() {
        return CommandType.LOGIN_REQUEST.getCommand();
    }
}
