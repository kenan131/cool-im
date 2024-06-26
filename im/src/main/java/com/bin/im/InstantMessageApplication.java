package com.bin.im;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.bin.im.mapper"})
public class InstantMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstantMessageApplication.class, args);
    }

}
