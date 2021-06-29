package cn.rt.common.netty.message;

import cn.rt.common.netty.message.login.LoginRequestMessage;
import cn.rt.common.netty.message.login.LoginResponseMessage;

/**
 * @author ruanting
 * @date 2021/6/25
 */
public enum CommandType {
    /**
     * 登录请求
     */
    LOGIN_REQUEST(1, LoginRequestMessage.class),
    /**
     * 登录返回
     */
    LOGIN_RESPONSE(2, LoginResponseMessage.class);


    /**
     * 登录请求
     */
    private Byte command;
    private Class messageType;

    public Byte getCommand() {
        return command;
    }

    public Class getMessageType() {
        return messageType;
    }

    public static Class getMessageType(byte command) {
        for (CommandType c : CommandType.values()) {
            if (c.getCommand() == command) {
                return c.getMessageType();
            }
        }
        return null;
    }

    CommandType(Integer command, Class messageType) {
        this.command = command.byteValue();
        this.messageType = messageType;
    }
}
