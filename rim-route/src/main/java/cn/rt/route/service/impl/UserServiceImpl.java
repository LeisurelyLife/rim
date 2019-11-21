package cn.rt.route.service.impl;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.entity.Useraccount;
import cn.rt.route.controller.RouteController;
import cn.rt.route.dao.UseraccountMapper;
import cn.rt.route.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
        String result = redisTemplate.opsForValue().get("");

        return null;
    }

    @Override
    public BaseResponse login(Useraccount useraccount) {
        //获取
        //
        return null;
    }

    @Override
    public String getRedisKey() {
        return null;
    }
}
