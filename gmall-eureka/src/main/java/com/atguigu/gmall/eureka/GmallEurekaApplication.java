package com.atguigu.gmall.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class GmallEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallEurekaApplication.class, args);
    }

}
