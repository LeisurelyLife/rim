package cn.rt.route;

import cn.rt.common.common.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("cn.rt.route.dao")
public class RimRouteApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RimRouteApplication.class, args);
        SpringContextHolder.setApplicationContext(context);
    }

}
