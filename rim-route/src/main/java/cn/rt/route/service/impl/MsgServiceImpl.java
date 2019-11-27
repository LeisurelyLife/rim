package cn.rt.route.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import cn.rt.common.common.RIMProtocol;
import cn.rt.route.service.MsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author ruanting
 * @date 2019/11/21
 */
@Service
public class MsgServiceImpl implements MsgService {

    private static final Logger log = LoggerFactory.getLogger(MsgServiceImpl.class);

    @Value("${server.send.interface}")
    private String sendInterface;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public BaseResponse sendMsg(RIMProtocol.RouteSendMsg rms) {
        String type = rms.getType();
        if (Constants.MSG_PERSONAL.equals(type)) {
            RIMProtocol.ServerSendMsg ssm = this.RP2SP(rms);
            BaseResponse response = this.sendPersonal(ssm);
            return response;
        } else if (Constants.MSG_GROUP.equals(type)) {
            RIMProtocol.ServerSendMsg ssm = this.RP2SP(rms);
            this.sendGroup(ssm);
            return null;
        } else {
            return null;
        }
    }

    private BaseResponse sendPersonal(RIMProtocol.ServerSendMsg ssm) {
        BaseResponse response = new BaseResponse();
        String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + ssm.getTargetUserId() + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() < 1) {
            return response;
        }
        String[] split = keys.toArray(new String[0])[0].split("\\|")[2].split(":");
        String url = "http://" + split[0] + ":" + split[1] + sendInterface;
        String auth = redisTemplate.opsForValue().get(Constants.HEAN_AUTH);
        JSONObject param = JSONUtil.parseObj(ssm);
        String result = HttpRequest.post(url)
                .header(Constants.HEAN_AUTH, auth)
                .header(Header.CONTENT_TYPE, "application/json; charset=utf-8")
                .body(param.toString())
                .timeout(20000)
                .execute().body();
        response = JSONUtil.toBean(result, BaseResponse.class);
        return response;
    }

    private BaseResponse sendGroup(RIMProtocol.ServerSendMsg ssm) {
        return null;
    }

    private RIMProtocol.ServerSendMsg RP2SP(RIMProtocol.RouteSendMsg rsm) {
        RIMProtocol.ServerSendMsg ssm = new RIMProtocol.ServerSendMsg();
        ssm.setType(rsm.getType());
        ssm.setUserId(rsm.getUserId());
        ssm.setMsg(rsm.getMsg());
        if (Constants.MSG_PERSONAL.equals(rsm.getType())) {
            ssm.setTargetUserId(rsm.getTargetId());
        } else if (Constants.MSG_GROUP.equals(rsm.getType())) {
            ssm.setTargetGroupId(rsm.getTargetId());
        }
        return ssm;
    }

}
