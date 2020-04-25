/**
 * Pengzu Tech Inc.
 * Copyright (c)) 2018 - 2020 All Right Reserved
 */
package com.github.jackieonway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author Jackie
 * @version \$id: WebSecurityConfig.java v 0.1 2020-04-25 10:53 Jackie Exp $$
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService detailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("zhangsan").password(passwordEncoder().encode("123")).authorities("p1").build());
        manager.createUser(User.withUsername("lisi").password(passwordEncoder().encode("456")).authorities("p2").build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/r/r1").hasAuthority("p1")
                .antMatchers("/r/r2").hasAuthority("p2")
                .antMatchers("/r/**").authenticated() // /r/** 下所有资源必须登录
                .anyRequest().permitAll() // 除了以上资源外全部放行
                .and()
                .formLogin()
                .successForwardUrl("/login-success");
    }
}
