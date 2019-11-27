package cn.rt.route.dao;

import cn.rt.common.entity.UserFriend;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface UserFriendMapper extends Mapper<UserFriend> {

    /**
     * 获取用户的好友列表ID
     * @param userId
     * @return
     */
    List<Map<String, Object>> getFriendList(@Param("userId") String userId);

    /**
     * 根据用户ID和好友ID查询双方是否为好友
     * @param map
     * @return
     */
    UserFriend searchUserFriend(@Param("map") Map<String, Object> map);



}