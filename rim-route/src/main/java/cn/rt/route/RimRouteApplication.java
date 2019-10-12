package cn.rt.route;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("cn.rt.route.dao")
public class RimRouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RimRouteApplication.class, args);
    }

}
