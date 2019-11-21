package cn.rt.route.service.impl;

import cn.hutool.json.JSONObject;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.entity.Useraccount;
import cn.rt.common.util.StringUtils;
import cn.rt.route.controller.RouteController;
import cn.rt.route.dao.UseraccountMapper;
import cn.rt.route.service.UserService;
import cn.rt.route.util.ZKModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanting
 * @date 2019/10/11
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<Useraccount> implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ZKModule zkModule;

    @Autowired
    private UseraccountMapper useraccountMapper;

    @Override
    public BaseResponse register(Useraccount useraccount) {
        useraccountMapper.insert(useraccount);
        BaseResponse response = new BaseResponse();
        response.setState(Constants.RESP_SUCCESS);
        return response;
    }

    @Override
    public BaseResponse isLogin(Useraccount useraccount) {
        BaseResponse baseResponse = new BaseResponse();
        String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + useraccount.getUserid() + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() < 1) {
            baseResponse.setMsg("未登陆");
            return baseResponse;
        }
        baseResponse.setState(Constants.RESP_SUCCESS);
        String key = keys.toArray(new String[0])[0];
        String[] split = key.split("|")[2].split(":");
        baseResponse.setData(split[0] + ":" + split[2]);
        return baseResponse;
    }

    @Override
    public BaseResponse login(Useraccount useraccount) {
        BaseResponse baseResponse = new BaseResponse();
        String randomServer = zkModule.getRandomServer();
        if (StringUtils.isEmpty(randomServer)) {
            baseResponse.setMsg("无可用服务器");
            return baseResponse;
        }
        String[] split = randomServer.split(":");
        String redisKey = Constants.REDIS_LOGIN_PREFIX + "|" + useraccount.getUserid() + "|" + split[0] + ":" + split[1] + ":" + split[2];
        redisTemplate.opsForValue().set(redisKey, useraccount.getUseraccount(), 60, TimeUnit.SECONDS);
        baseResponse.setState(Constants.RESP_SUCCESS);
        JSONObject data = new JSONObject();
        data.put("socketServer", split[0]);
        data.put("socketPort", split[2]);
        baseResponse.setData(data);
        return baseResponse;
    }

    @Override
    public String getRedisKey() {
        return null;
    }
}
