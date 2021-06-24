package cn.rt.route.controller;

import cn.hutool.json.JSONObject;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.common.RIMProtocol;
import cn.rt.common.entity.UserFriend;
import cn.rt.common.entity.UserAccount;
import cn.rt.common.util.InterUtil;
import cn.rt.common.util.StringUtils;
import cn.rt.common.util.UuidUtils;
import cn.rt.route.service.MsgService;
import cn.rt.route.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author ruanting
 * @date 2019/10/11
 */
@Controller
@RequestMapping(value = "/")
public class RouteController {

    private static final Logger log = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Autowired
    private MsgService msgService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse register(@RequestBody Map<String, Object> paramMap) {
        BaseResponse response = new BaseResponse();
        log.info("开始新增用户");
        try {
            String account = StringUtils.getObjStr(paramMap.get("userAccount"));
            String password = StringUtils.getObjStr(paramMap.get("password"));
            UserAccount useraccount = new UserAccount();
            useraccount.setUserAccount(account);
            useraccount = userService.selectOne(useraccount);
            if (useraccount != null) {
                response.setState(Constants.RESP_FAIL);
                response.setMsg(InterUtil.interInfo(request, "user.error.exist"));
                return response;
            }
            useraccount = new UserAccount();
            useraccount.setUserAccount(account);
            useraccount.setUserPassword(password);
            useraccount.setUserId(UuidUtils.getRandomUuidWithoutSeparator());
            userService.register(useraccount);
        } catch (Exception e) {
            log.error("新增用户失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            return response;
        }
        log.info("新增用户成功");
        response.setState(Constants.RESP_SUCCESS);
        response.setMsg(InterUtil.interInfo(request, "common.operSucc"));
        response.setCode(Constants.CODE_SUCCESS);
        return response;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse login(@RequestBody UserAccount userAccount) {
        BaseResponse response = new BaseResponse();
        log.info("用户开始登录");
        try {
            UserAccount param = new UserAccount();
            param.setUserAccount(userAccount.getUserAccount());
            param = userService.selectOne(param);
            if (param == null) {
                response.setState(Constants.RESP_FAIL);
                response.setMsg(InterUtil.interInfo(request, "user.error.notexist"));
                return response;
            }

            if (!param.getUserPassword().equals(userAccount.getUserPassword())) {
                response.setState(Constants.RESP_FAIL);
                response.setMsg(InterUtil.interInfo(request, "user.error.password"));
                return response;
            }

            BaseResponse isLogin = userService.isLogin(param);
            if (Constants.RESP_SUCCESS.equals(isLogin.getState())) {
                return isLogin;
            }

            BaseResponse login = userService.login(param);
            if (Constants.RESP_SUCCESS.equals(login.getState())) {
                response.setState(Constants.RESP_SUCCESS);
                response.setCode(Constants.CODE_SUCCESS);
                response.setData(login.getData());
                return response;
            }
            log.info("用户登录成功");
            return response;
        } catch (Exception e) {
            log.error("用户登录失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setCode(Constants.CODE_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            return response;
        }
    }

    @RequestMapping(value = "/friendList", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse friendList(@RequestBody Map<String, Object> paramMap) {
        BaseResponse response = new BaseResponse();
        log.info("开始获取好友列表");
        try {
            String userId = StringUtils.getObjStr(paramMap.get("userId"));
            List<Map<String, Object>> friendList = userService.getFriend(userId);
            JSONObject data = new JSONObject();
            data.put("friendList", friendList);
            response.setData(data);
            response.setState(Constants.RESP_SUCCESS);
            response.setCode(Constants.CODE_SUCCESS);
            log.info("获取好友列表成功");
            return response;
        } catch (Exception e) {
            log.error("用户获取好友列表失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setCode(Constants.CODE_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            return response;
        }
    }

    @RequestMapping(value = "/searchNum", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse searchNum(@RequestBody Map<String, Object> paramMap) {
        BaseResponse response = new BaseResponse();
        log.info("开始查询账号");
        try {
            String type = StringUtils.getObjStr(paramMap.get("type"));
            String number = StringUtils.getObjStr(paramMap.get("number"));
            if ("01".equals(type)) {
                UserAccount useraccount = new UserAccount();
                useraccount.setUserAccount(number);
                useraccount = userService.selectOne(useraccount);
                response.setState(Constants.RESP_SUCCESS);
                response.setCode(Constants.CODE_SUCCESS);
                if (useraccount != null) {
                    JSONObject data = new JSONObject();
                    data.put("type", type);
                    data.put("targetNum", useraccount.getUserAccount());
                    data.put("targetName", useraccount.getUserName());
                    response.setData(data);
                }
                return response;
            } else if ("02".equals(type)) {
                return response;
            }
            log.info("查询账号成功");
            return response;
        } catch (Exception e) {
            log.error("查询账号失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setCode(Constants.CODE_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            return response;
        }
    }

    @RequestMapping(value = "/addFriend", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse addFriend(@RequestBody Map<String, Object> paramMap) {
        BaseResponse response = new BaseResponse();
        log.info("开始添加好友");
        try {
            String userId = StringUtils.getObjStr(paramMap.get("userId"));
            String targetNum = StringUtils.getObjStr(paramMap.get("targetNum"));

            UserAccount friend = new UserAccount();
            friend.setUserAccount(targetNum);
            friend = userService.selectOne(friend);
            UserFriend userFriend = userService.searchUserFriend(userId, friend.getUserId());
            if (userFriend != null) {
                response.setState(Constants.RESP_FAIL);
                response.setCode(Constants.CODE_FAIL);
                response.setMsg("好友已存在");
                return response;
            }
            userFriend = new UserFriend();
            userFriend.setUserId(userId);
            userFriend.setFriendId(friend.getUserId());
            userService.saveUserFriend(userFriend);

            response.setCode(Constants.CODE_SUCCESS);
            response.setState(Constants.RESP_SUCCESS);
            log.info("添加好友成功");
            return response;
        } catch (Exception e) {
            log.error("添加好友失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setCode(Constants.CODE_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            return response;
        }
    }

    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse sendMsg(@RequestBody RIMProtocol.RouteSendMsg msg) {
        BaseResponse response = new BaseResponse();
        try {
            response = msgService.sendMsg(msg);
            response.setCode(Constants.CODE_SUCCESS);
            return response;
        } catch (Exception e) {
            log.error("发送消息失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            response.setCode(Constants.CODE_FAIL);
            return response;
        }
    }

}
