package cn.rt.server.controller;

import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.common.RIMProtocol;
import cn.rt.server.service.MsgService;
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

/**
 * @author ruanting
 * @date 2019/10/12
 */
@Controller
@RequestMapping("/")
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MsgService msgService;

    @RequestMapping(value = "/testIndex")
    @ResponseBody
    public String testIndex() {
        return "index success";
    }

    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse sendMsg(@RequestBody RIMProtocol.ServerSendMsg ssm) {
        BaseResponse response = new BaseResponse();
        try {
            String auth = request.getHeader(Constants.HEAN_AUTH);
            String rAuth = redisTemplate.opsForValue().get(Constants.HEAN_AUTH);
            if (!auth.equals(rAuth)) {
                response.setCode(Constants.CODE_FAIL);
                return response;
            }
            BaseResponse sendResp = msgService.sendMsg(ssm);
            return sendResp;
        } catch (Exception e) {
            log.error("发送消息失败", e);
            response.setCode(Constants.CODE_FAIL);
            return response;
        }
    }

}
