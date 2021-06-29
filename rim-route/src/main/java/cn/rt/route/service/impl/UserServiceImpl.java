package cn.rt.route.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.entity.UserAccount;
import cn.rt.common.entity.UserFriend;
import cn.rt.common.util.StringUtils;
import cn.rt.route.dao.UserAccountMapper;
import cn.rt.route.dao.UserFriendMapper;
import cn.rt.route.service.UserService;
import cn.rt.route.util.ZKModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanting
 * @date 2019/10/11
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserAccount> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ZKModule zkModule;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private UserFriendMapper userFriendMapper;

    @Override
    public BaseResponse register(UserAccount useraccount) {
        userAccountMapper.insert(useraccount);
        BaseResponse response = new BaseResponse();
        response.setState(Constants.RESP_SUCCESS);
        return response;
    }

    @Override
    public BaseResponse isLogin(UserAccount useraccount) {
        BaseResponse baseResponse = new BaseResponse();
        String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + useraccount.getUserId() + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() < 1) {
            baseResponse.setMsg("未登陆");
            return baseResponse;
        }
        baseResponse.setState(Constants.RESP_SUCCESS);
        String key = keys.toArray(new String[0])[0];
        String value = redisTemplate.opsForValue().get(key);
        String[] kSplit = key.split("\\|")[2].split(":");
        String[] vSplit = value.split("\\|");
        JSONObject data = new JSONObject();
        data.put("socketServer", kSplit[0]);
        data.put("socketPort", kSplit[2]);
        data.put("userId", useraccount.getUserId());
        data.put("token", vSplit[0]);
        baseResponse.setData(data);
        return baseResponse;
    }

    @Override
    public BaseResponse login(UserAccount useraccount) {
        BaseResponse baseResponse = new BaseResponse();
        String randomServer = zkModule.getRandomServer();
        if (StringUtils.isEmpty(randomServer)) {
            baseResponse.setMsg("无可用服务器");
            return baseResponse;
        }
        String[] split = randomServer.split(":");
        String redisKey = Constants.REDIS_LOGIN_PREFIX + "|" + useraccount.getUserId() + "|" + split[0] + ":" + split[1] + ":" + split[2];
        String token = IdUtil.simpleUUID();
        String redisValue = token;
        redisTemplate.opsForValue().set(redisKey, redisValue, 60, TimeUnit.SECONDS);
        baseResponse.setState(Constants.RESP_SUCCESS);
        JSONObject data = new JSONObject();
        data.put("socketServer", split[0]);
        data.put("socketPort", split[2]);
        data.put("userId", useraccount.getUserId());
        data.put("token", token);
        data.put("userName", useraccount.getUserName());
        baseResponse.setData(data);
        return baseResponse;
    }

    @Override
    public List<Map<String, Object>> getFriend(String userId) {
        List<Map<String, Object>> friendList = userFriendMapper.getFriendList(userId);
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> friend : friendList) {
            String friendId = StringUtils.getObjStr(friend.get("friendId"));
            String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + friendId + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys.size() < 1) {
                friend.put("state", Constants.User.USER_LOGIN_STATE_OFF.getValue());
            } else {
                friend.put("state", Constants.User.USER_LOGIN_STATE_ON.getValue());
            }
            result.add(friend);
        }
        return result;
    }

    @Override
    public UserFriend searchUserFriend(String userId, String friendId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("friendId", friendId);
        return userFriendMapper.searchUserFriend(map);
    }

    @Override
    public void saveUserFriend(UserFriend userFriend) {
        userFriendMapper.insert(userFriend);
    }

}
