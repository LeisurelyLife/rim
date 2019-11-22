package cn.rt.route.service;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.entity.Useraccount;

import java.util.List;
import java.util.Map;

/**
 * @author ruanting
 * @date 2019/10/11
 */
public interface UserService extends BaseService<Useraccount> {

    /**
     * 用户注册
     * @param useraccount
     * @return
     */
    BaseResponse register(Useraccount useraccount);

    /**
     * 查看用户是否登录
     * @param useraccount
     * @return
     */
    BaseResponse isLogin(Useraccount useraccount);

    /**
     * 用户登录
     * @param useraccount
     * @return
     */
    BaseResponse login(Useraccount useraccount);

    String getRedisKey();

    List<Map<String, Object>> getFriend(String userId);

}
