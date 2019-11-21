package cn.rt.route.controller;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.entity.Useraccount;
import cn.rt.common.util.InterUtil;
import cn.rt.common.util.StringUtils;
import cn.rt.common.util.UuidUtils;
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
import java.util.Map;

/**
 * @author ruanting
 * @date 2019/10/11
 */
@Controller
@RequestMapping(value = "/")
public class RouteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse register(@RequestBody Map<String, Object> paramMap) {
        BaseResponse response = new BaseResponse();
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
            LOGGER.error("新增用户失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            return response;
        }
        response.setState(Constants.RESP_SUCCESS);
        response.setMsg(InterUtil.interInfo(request, "common.operSucc"));
        return response;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse login(@RequestBody Map<String, Object> paramMap) {
        BaseResponse response = new BaseResponse();
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
                String serverAddr = StringUtils.getObjStr(isLogin.getData());
                response.setState(Constants.RESP_SUCCESS);
                response.setData(serverAddr);
                return response;
            }

            BaseResponse login = userService.login(useraccount);
            if (Constants.RESP_SUCCESS.equals(login.getState())) {
                response.setState(Constants.RESP_SUCCESS);
                response.setData(login.getData());
                return response;
            }
            return response;
        } catch (Exception e) {
            LOGGER.error("新增用户失败", e);
            response.setState(Constants.RESP_FAIL);
            response.setMsg(InterUtil.interInfo(request, "common.operFail"));
            return response;
        }
    }

}
