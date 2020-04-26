/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.sql.DataSource;

/**
 * @author Jackie
 * @version $id: AuthrizationServerConfig.java v 0.1 2020-04-23 14:55 Jackie Exp $$
 */
@Configuration
// 开启授权认证服务
@EnableAuthorizationServer
public class AuthrizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public TokenStore tokenStore(){
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcClientDetailsService jdbcClientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许表单认证
        security.allowFormAuthenticationForClients();
        //允许check_ token访问
        security.checkTokenAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    //配置appId、appKey、回调地址、token有效期的
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        String clientId = "client_1";
//        clients.inMemory().withClient(clientId) // appId
//        .secret(passwordEncoder().encode("123456")) // appKey
//        .redirectUris("http://www.baidu.com") //回调地址
//        .authorizedGrantTypes ("authorization_code","password","refresh_token") //授权模式为： 授权码
//        .scopes("all") //授权范围: all：表示所有权限
//        .accessTokenValiditySeconds(86400) //token 有效期 86400秒 即1天
//        .refreshTokenValiditySeconds(86400) //refreshToken 有效期 86400秒 即1天
//        ;
        clients.withClientDetails(jdbcClientDetailsService());
    }

    //设置token类型
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager())
                .tokenStore(tokenStore())
                // 必须加上此段代码，否则refresh_token 会报错
                .userDetailsService(userDetailsService)
                .allowedTokenEndpointRequestMethods (HttpMethod.GET, HttpMethod.POST);
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        AuthenticationManager authenticationManager = new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return daoAuhthenticationProvider().authenticate( authentication);
            }
        };
        return authenticationManager;

    }

    @Bean
    public AuthenticationProvider daoAuhthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider . setUserDetailsService(userDetailsService) ;
        daoAuthenticationProvider . setHideUserNotFoundExceptions(false);
        daoAuthenticationProvider . setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }


    //设置添加用户信息,正常应该从数据库中读取
//    @Bean
//    UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
//        userDetailsService. createUser(User.withUsername("user1").password(passwordEncoder().encode("123456"))
//                .authorities("ROLE_ USER") . build());
//        userDetailsService . createUser(User.withUsername("user2").password(passwordEncoder().encode("1234567"))
//                .authorities("ROLE_ USER") . build());
//        return userDetailsService;
//    }
}
