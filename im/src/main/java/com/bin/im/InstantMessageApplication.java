package com.bin.im;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.bin.im.mapper"})
@EnableDubbo
public class InstantMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstantMessageApplication.class, args);
    }

}
