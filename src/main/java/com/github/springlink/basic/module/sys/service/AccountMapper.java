package com.github.springlink.basic.module.sys.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.github.springlink.basic.module.sys.domain.Account;
import com.github.springlink.basic.module.sys.domain.AccountToken;
import com.github.springlink.basic.module.sys.dto.AccountAdd;
import com.github.springlink.basic.module.sys.dto.AccountChangeProfile;
import com.github.springlink.basic.module.sys.dto.AccountLoginReply;
import com.github.springlink.basic.module.sys.dto.AccountReply;

@Mapper(componentModel = "spring")
public interface AccountMapper {
	Account addToEntity(AccountAdd add);

	@Mapping(target = "token", source = "accountToken.id")
	AccountLoginReply entityToLoginReply(Account account, AccountToken accountToken);

	AccountReply entityToReply(Account entity);

	@Mapping(target = "id", ignore = true)
	void changeProfileToEntity(AccountChangeProfile apc, @MappingTarget Account account);
}
