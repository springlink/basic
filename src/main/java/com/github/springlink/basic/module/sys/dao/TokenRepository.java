package com.github.springlink.basic.module.sys.dao;

import org.springframework.data.repository.CrudRepository;

import com.github.springlink.basic.module.sys.domain.Token;

public interface TokenRepository extends CrudRepository<Token, String> {
}
