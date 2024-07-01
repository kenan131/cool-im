package com.example.router.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("capi/router")
public class RouterController {
    @GetMapping("test")
    public String getRouter(){
        System.out.println("post");
        return "数据！";
    }

    @GetMapping("public")
    public void test(){
        System.out.println("get");
    }
}
