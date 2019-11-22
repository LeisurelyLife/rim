package cn.rt.route.dao;

import cn.rt.common.entity.UserFriend;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserFriendMapper extends Mapper<UserFriend> {

    /**
     * 获取用户的好友列表ID
     * @param userId
     * @return
     */
    List<String> getFriendId(@Param("userId") String userId);

}