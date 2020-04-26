/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.config;

import com.github.jackieonway.dao.CredentialDao;
import com.github.jackieonway.entity.Credentials;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Jackie
 * @version $id: JdbcUserDetailServiceImpl.java v 0.1 2020-04-23 17:29 Jackie Exp $$
 */
@Service
public class JdbcUserDetailServiceImpl implements UserDetailsService {

    @Resource
    private CredentialDao credentialDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credentials credentials = credentialDao.selectByUserName(username);
        if (Objects.isNull(credentials)){
            throw new UsernameNotFoundException("用户名[" + username + "]没有找到");
        }
        return new User(credentials.getName(),credentials.getPassword(),credentials.isEnabled(),
                true,true,true,credentials.getAuthorities());
    }
}
