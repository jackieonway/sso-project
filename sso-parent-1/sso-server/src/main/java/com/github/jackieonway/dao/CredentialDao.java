/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.dao;

import com.github.jackieonway.entity.Credentials;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Jackie
 * @version $id: CredentialDao.java v 0.1 2020-04-22 16:35 Jackie Exp $$
 */
@Repository
public interface CredentialDao {

    Credentials selectByUserName(@Param("username") String username);
}
