package com.github.springlink.basic.module.sys.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.github.springlink.basic.module.sys.domain.Token;
import com.github.springlink.basic.module.sys.domain.User;
import com.github.springlink.basic.module.sys.dto.MyProfileReply;
import com.github.springlink.basic.module.sys.dto.MySetProfile;
import com.github.springlink.basic.module.sys.dto.UserAdd;
import com.github.springlink.basic.module.sys.dto.UserAuth;
import com.github.springlink.basic.module.sys.dto.UserLoginReply;
import com.github.springlink.basic.module.sys.dto.UserReply;
import com.github.springlink.basic.module.sys.dto.UserSetProfile;

@Mapper(componentModel = "spring")
public interface UserMapper {
	@Mapping(target = "locked", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	@Mapping(target = "builtIn", constant = "false")
	User addToEntity(UserAdd source);

	@Mapping(target = "userId", source = "id")
	@Mapping(target = "permissions", ignore = true)
	UserAuth entityToAuth(User source);

	UserReply entityToReply(User source);

	@Mapping(target = "auth", ignore = true)
	@Mapping(target = "token", source = "id")
	UserLoginReply tokenToLoginReply(Token source);

	@Mapping(target = "locked", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	void setProfileToEntity(UserSetProfile source, @MappingTarget User orElseThrow);

	MyProfileReply entityToMyProfileReply(User source);

	@Mapping(target = "locked", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	void mySetProfileToEntity(MySetProfile body, @MappingTarget User orElseThrow);
}
