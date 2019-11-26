package cn.rt.route.interceptor;

import cn.hutool.json.JSONObject;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * @author ruanting
 * @date 2019/11/26
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("token");
        String userId = request.getParameter("userId");

        String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + userId + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() < 1) {
            this.failReturn(response);
            return false;
        }
        String key = keys.toArray(new String[0])[0];
        String value = redisTemplate.opsForValue().get(key);
        String rToken = value.split("\\|")[0];

        if (!token.equals(rToken)) {
            this.failReturn(response);
            return false;
        }

        return true;
    }

    private void failReturn(HttpServletResponse response) {
        try {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setState(Constants.RESP_FAIL);
            baseResponse.setCode(Constants.CODE_FAIL);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            JSONObject json = new JSONObject(baseResponse);
            writer.write(json.toString());
        }catch (IOException e){
            log.error("IO异常:" + e.getMessage(), e);
        }
    }

}
