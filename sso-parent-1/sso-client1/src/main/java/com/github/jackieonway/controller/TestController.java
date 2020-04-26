package com.github.jackieonway.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/normal")
    @PreAuthorize("hasAuthority('ROLE_PRODUCT_ADMIN')")
    public String normal( ) {
        return "normal permission test success !!!";
    }

    @GetMapping("/medium")
    @PreAuthorize("hasAuthority('ROLE_RESOURCE_ADMIN')")
    public String medium() {
        return "medium permission test success !!!";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_OAUTH_ADMIN')")
    public String admin() {
        return "admin permission test success !!!";
    }

    @GetMapping("/callback")
    public String callback(String code,HttpServletResponse response) throws IOException {
        System.out.println("code : " + code);

//        response.sendRedirect("http://localhost:8080");
        return code;
    }

    @GetMapping("/connect")
    public void connect(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.sendRedirect("http://192.168.110.117:8081/auth/oauth/authorize?response_type=code&client_id=curl_client");
    }
}