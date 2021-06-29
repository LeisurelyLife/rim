package cn.rt.server.service;

import cn.rt.common.netty.message.ResponseMessage;
import cn.rt.common.netty.message.login.LoginRequestMessage;

/**
 * @author ruanting
 * @date 2021/6/28
 */
public interface LoginService {
    /**
     * 客户端登录连接
     * @param message
     * @return
     */
    ResponseMessage login(LoginRequestMessage message);
}
