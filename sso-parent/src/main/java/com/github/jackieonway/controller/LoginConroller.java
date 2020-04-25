/**
 * Pengzu Tech Inc.
 * Copyright (c)) 2018 - 2020 All Right Reserved
 */
package com.github.jackieonway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jackie
 * @version \$id: LoginConroller.java v 0.1 2020-04-25 10:58 Jackie Exp $$
 */
@RestController
public class LoginConroller {

    @PostMapping("/login-success")
    public String loginSuccess(){
        return "登录成功";
    }

    @GetMapping("/r/r1")
    public String r1(){
        return "访问资源r1";
    }
    @GetMapping("/r/r2")
    public String r2(){
        return "访问资源r2";
    }

}
