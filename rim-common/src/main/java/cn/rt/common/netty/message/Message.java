package cn.rt.common.netty.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ruanting
 * @date 2021/6/25
 */
@Getter
@Setter
public abstract class Message {
    /**
     * 协议版本
     */
    private Byte version = 1;

    /**
     * 获取消息类型
     * @return
     */
    public abstract Byte getCommand();

}
