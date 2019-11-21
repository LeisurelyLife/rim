package cn.rt.route.service;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.entity.Useraccount;

/**
 * @author ruanting
 * @date 2019/10/11
 */
public interface UserService extends BaseService<Useraccount> {

    BaseResponse register(Useraccount useraccount);

    BaseResponse isLogin(Useraccount useraccount);

    BaseResponse login(Useraccount useraccount);

    String getRedisKey();

}
