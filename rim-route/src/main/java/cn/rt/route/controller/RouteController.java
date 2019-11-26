package cn.rt.route.controller;

import cn.hutool.json.JSONObject;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.common.RIMProtocol;
import cn.rt.common.entity.Useraccount;
import cn.rt.common.util.InterUtil;
import cn.rt.common.util.StringUtils;
import cn.rt.common.util.UuidUtils;
import cn.rt.route.service.MsgService;
import cn.rt.route.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate<String, String> redisTemplate;

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
            Useraccount useraccount = new Useraccount();
            useraccount.setUseraccount(account);
            useraccount = userService.selectOne(useraccount);
            if (useraccount != null) {
                response.setState(Constants.RESP_FAIL);
                response.setMsg(InterUtil.interInfo(request, "user.error.exist"));
                return response;
            }
            useraccount = new Useraccount();
            useraccount.setUseraccount(account);
            useraccount.setPassword(password);
            useraccount.setUserid(UuidUtils.getRandomUuidWithoutSeparator());
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
        return response;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse login(@RequestBody Map<String, Object> paramMap) {
        BaseResponse response = new BaseResponse();
        log.info("用户开始登录");
        try {
            String account = StringUtils.getObjStr(paramMap.get("userAccount"));
            String password = StringUtils.getObjStr(paramMap.get("password"));
            Useraccount useraccount = new Useraccount();
            useraccount.setUseraccount(account);
            useraccount = userService.selectOne(useraccount);
            if (useraccount == null) {
                response.setState(Constants.RESP_FAIL);
                response.setMsg(InterUtil.interInfo(request, "user.error.notexist"));
                return response;
            }

            if (!useraccount.getPassword().equals(password)) {
                response.setState(Constants.RESP_FAIL);
                response.setMsg(InterUtil.interInfo(request, "user.error.password"));
                return response;
            }

            BaseResponse isLogin = userService.isLogin(useraccount);
            if (Constants.RESP_SUCCESS.equals(isLogin.getState())) {
                return isLogin;
            }

            BaseResponse login = userService.login(useraccount);
            if (Constants.RESP_SUCCESS.equals(login.getState())) {
                response.setState(Constants.RESP_SUCCESS);
                response.setData(login.getData());
                return response;
            }
            log.info("用户登录成功");
            return response;
        } catch (Exception e) {
            log.error("用户登录失败", e);
            response.setState(Constants.RESP_FAIL);
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
            log.info("获取好友列表成功");
            return response;
        } catch (Exception e) {
            log.error("用户获取好友列表失败", e);
            response.setState(Constants.RESP_FAIL);
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
            return response;
        } catch (Exception e) {
            log.error("发送消息失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            return response;
        }
    }

}
