package sourcefx.module.sys.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import sourcefx.module.sys.domain.UserToken;
import sourcefx.module.sys.domain.User;
import sourcefx.module.sys.dto.auth.UserAuth;
import sourcefx.module.sys.dto.auth.UserLoginReply;
import sourcefx.module.sys.dto.my.MyProfileReply;
import sourcefx.module.sys.dto.my.MySetProfile;
import sourcefx.module.sys.dto.user.UserAdd;
import sourcefx.module.sys.dto.user.UserReply;
import sourcefx.module.sys.dto.user.UserSetProfile;

@Mapper(componentModel = "spring")
public interface UserConverter {
	@Mapping(target = "locked", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	@Mapping(target = "builtIn", constant = "false")
	User convert(UserAdd source);

	@Mapping(target = "userId", source = "id")
	@Mapping(target = "permissions", ignore = true)
	UserAuth convertToAuth(User source);

	UserReply convertToReply(User source);

	@Mapping(target = "auth", ignore = true)
	UserLoginReply convert(UserToken source);

	@Mapping(target = "locked", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	void convert(UserSetProfile source, @MappingTarget User orElseThrow);

	MyProfileReply entityToMyProfileReply(User source);

	@Mapping(target = "locked", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "roleIds", ignore = true)
	void convert(MySetProfile body, @MappingTarget User orElseThrow);
}
