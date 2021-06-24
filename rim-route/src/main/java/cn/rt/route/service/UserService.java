package cn.rt.route.service;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.entity.UserFriend;
import cn.rt.common.entity.UserAccount;

import java.util.List;
import java.util.Map;

/**
 * @author ruanting
 * @date 2019/10/11
 */
public interface UserService extends BaseService<UserAccount> {

    /**
     * 用户注册
     * @param useraccount
     * @return
     */
    BaseResponse register(UserAccount useraccount);

    /**
     * 查看用户是否登录
     * @param useraccount
     * @return
     */
    BaseResponse isLogin(UserAccount useraccount);

    /**
     * 用户登录
     * @param useraccount
     * @return
     */
    BaseResponse login(UserAccount useraccount);

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    List<Map<String, Object>> getFriend(String userId);

    /**
     * 查询双方是否为好友
     * @param userId
     * @param friendId
     * @return
     */
    UserFriend searchUserFriend(String userId, String friendId);

    /**
     * 保存好友列表
     * @param userFriend
     */
    void saveUserFriend(UserFriend userFriend);

}
