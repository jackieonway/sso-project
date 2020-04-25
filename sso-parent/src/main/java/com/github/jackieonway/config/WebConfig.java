/**
 * Pengzu Tech Inc.
 * Copyright (c)) 2018 - 2020 All Right Reserved
 */
package com.github.jackieonway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Jackie
 * @version \$id: WebConfig.java v 0.1 2020-04-25 11:00 Jackie Exp $$
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/login");
    }
}
