package com.github.springlink.basic.module.sys.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.github.springlink.basic.module.sys.domain.User;
import com.github.springlink.basic.module.sys.domain.UserToken;
import com.github.springlink.basic.module.sys.dto.UserAdd;
import com.github.springlink.basic.module.sys.dto.UserAuth;
import com.github.springlink.basic.module.sys.dto.UserChangeProfile;
import com.github.springlink.basic.module.sys.dto.UserLoginReply;
import com.github.springlink.basic.module.sys.dto.UserReply;

@Mapper(componentModel = "spring")
public interface UserMapper {
	@Mapping(target = "roleIds", ignore = true)
	User addToEntity(UserAdd source);

	@Mapping(target = "userId", source = "id")
	@Mapping(target = "permissions", ignore = true)
	UserAuth entityToAuth(User source);

	UserReply entityToReply(User source);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	void changeProfileToEntity(UserChangeProfile source, @MappingTarget User target);

	@Mapping(target = "auth", ignore = true)
	@Mapping(target = "token", source = "id")
	UserLoginReply tokenToLoginReply(UserToken source);
}
