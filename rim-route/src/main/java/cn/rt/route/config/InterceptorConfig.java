package cn.rt.route.config;

import cn.rt.route.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

/**
 * @author ruanting
 * @date 2019/11/26
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        ArrayList<String> excludePathPatterns = new ArrayList<>();
        excludePathPatterns.add("/register");
        excludePathPatterns.add("/login");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(excludePathPatterns);
    }

}
