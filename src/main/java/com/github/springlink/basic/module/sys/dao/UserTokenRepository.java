package com.github.springlink.basic.module.sys.dao;

import org.springframework.data.repository.CrudRepository;

import com.github.springlink.basic.module.sys.domain.UserToken;

public interface UserTokenRepository extends CrudRepository<UserToken, String> {
}
