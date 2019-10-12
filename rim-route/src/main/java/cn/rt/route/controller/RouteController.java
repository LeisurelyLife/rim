package cn.rt.route.controller;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.entity.Useraccount;
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
            return response;
        }
        response.setState(Constants.RESP_SUCCESS);
        return response;
    }

}
