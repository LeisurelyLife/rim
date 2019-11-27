package cn.rt.route;

import cn.rt.common.common.SpringContextHolder;
import cn.rt.route.util.ZKModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan("cn.rt.route.dao")
public class RimRouteApplication implements CommandLineRunner {

    @Autowired
    private ZKModule zkModule;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RimRouteApplication.class, args);
        SpringContextHolder.setApplicationContext(context);
    }

    @Override
    public void run(String... args) throws Exception {
        zkModule.subscribeEvent();
    }
}
