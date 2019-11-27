package cn.rt.route.filter;

import cn.hutool.json.JSONObject;
import cn.rt.common.common.BaseResponse;
import cn.rt.common.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ruanting
 * @date 2019/11/27
 */
@WebFilter(urlPatterns = "/*", filterName = "loginFilter")
public class LoginFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);

    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/login", "/register")));

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LoginFilterSwapper loginFilterSwapper = null;
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
            //排除的接口
            if (ALLOWED_PATHS.contains(path)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            loginFilterSwapper = new LoginFilterSwapper((HttpServletRequest) servletRequest);
            String body = loginFilterSwapper.getBody();
            JSONObject param = new JSONObject(body);
            String token = param.getStr("token");
            String userId = param.getStr("userId");

            String pattern = Constants.REDIS_LOGIN_PREFIX + "|" + userId + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys.size() < 1) {
                this.failReturn(response);
                return;
            }
            String key = keys.toArray(new String[0])[0];
            String value = redisTemplate.opsForValue().get(key);
            String rToken = value.split("\\|")[0];

            if (!token.equals(rToken)) {
                this.failReturn(response);
                return;
            }
        }

        if (loginFilterSwapper == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(loginFilterSwapper, servletResponse);
        }
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
