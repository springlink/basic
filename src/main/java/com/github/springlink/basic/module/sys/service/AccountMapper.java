package com.github.springlink.basic.module.sys.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.github.springlink.basic.module.sys.domain.Account;
import com.github.springlink.basic.module.sys.domain.AccountToken;
import com.github.springlink.basic.module.sys.dto.AccountAdd;
import com.github.springlink.basic.module.sys.dto.AccountAuth;
import com.github.springlink.basic.module.sys.dto.AccountChangeProfile;
import com.github.springlink.basic.module.sys.dto.AccountLoginReply;
import com.github.springlink.basic.module.sys.dto.AccountReply;

@Mapper(componentModel = "spring")
public interface AccountMapper {
	@Mapping(target = "roleIds", ignore = true)
	Account addToEntity(AccountAdd add);

	@Mapping(target = "userId", source = "id")
	@Mapping(target = "permissions", ignore = true)
	AccountAuth entityToAuthReply(Account account);

	AccountReply entityToReply(Account entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	void changeProfileToEntity(AccountChangeProfile apc, @MappingTarget Account account);

	@Mapping(target = "auth", ignore = true)
	@Mapping(target = "token", source = "id")
	AccountLoginReply tokenToLoginReply(AccountToken accountToken);
}
