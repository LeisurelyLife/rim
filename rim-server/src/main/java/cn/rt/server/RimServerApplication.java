package cn.rt.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RimServerApplication implements CommandLineRunner {

    @Autowired
    private ZKRegister zkRegister;

    public static void main(String[] args) {
        SpringApplication.run(RimServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        zkRegister.regist();
    }
}
