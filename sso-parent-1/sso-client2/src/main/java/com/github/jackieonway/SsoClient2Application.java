package com.github.jackieonway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SsoClient2Application {

    public static void main(String[] args) {
        SpringApplication.run(SsoClient2Application.class, args);
    }

}
