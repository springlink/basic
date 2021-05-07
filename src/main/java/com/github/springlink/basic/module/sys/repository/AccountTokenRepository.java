package com.github.springlink.basic.module.sys.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.springlink.basic.module.sys.domain.AccountToken;

public interface AccountTokenRepository extends CrudRepository<AccountToken, String> {
}
