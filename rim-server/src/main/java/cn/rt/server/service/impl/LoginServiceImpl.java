package cn.rt.server.service.impl;

import cn.rt.common.common.Constants;
import cn.rt.common.netty.message.ResponseMessage;
import cn.rt.common.netty.message.ResponseMessageCode;
import cn.rt.common.netty.message.login.LoginRequestMessage;
import cn.rt.common.netty.message.login.LoginResponseMessage;
import cn.rt.common.util.IPUtil;
import cn.rt.server.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author ruanting
 * @date 2021/6/28
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public ResponseMessage login(LoginRequestMessage message) {
        LoginResponseMessage response = new LoginResponseMessage();
        String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + message.getUserId() + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() < 1) {
            response.setRespDesc("用户未登陆");
            response.setRespResult(ResponseMessage.RESP_FAILURE);
            response.setRespCode(ResponseMessageCode.NOT_LOGIN);
            return response;
        }
        String key = keys.toArray(new String[0])[0];
        String value = redisTemplate.opsForValue().get(key);
        String[] kSplit = key.split("\\|")[2].split(":");
        String token = value;
        if (!token.equals(message.getToken())) {
            response.setRespDesc("token错误");
            response.setRespResult(ResponseMessage.RESP_FAILURE);
            response.setRespCode(ResponseMessageCode.TOKEN_MISTAKE);
            return response;
        }
        String socketServer = kSplit[0];
        String localIp = IPUtil.getInternetIp();
        if (!socketServer.equals(localIp)) {
            response.setRespDesc("服务器分配IP校验错误");
            response.setRespResult(ResponseMessage.RESP_FAILURE);
            response.setRespCode(ResponseMessageCode.SERVER_IP_MISTAKE);
            return response;
        }

        return response;
    }
}
