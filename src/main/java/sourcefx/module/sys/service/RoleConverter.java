package sourcefx.module.sys.service;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import sourcefx.core.permission.Permission;
import sourcefx.module.sys.domain.Role;
import sourcefx.module.sys.dto.role.PermissionReply;
import sourcefx.module.sys.dto.role.RoleAdd;
import sourcefx.module.sys.dto.role.RoleReply;
import sourcefx.module.sys.dto.role.RoleSetDetail;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleConverter {
	@Mapping(target = "disabled", ignore = true)
	Role convert(RoleAdd add);

	RoleReply convert(Role entity);

	@Mapping(target = "disabled", ignore = true)
	void convert(RoleSetDetail setDetail, @MappingTarget Role role);

	PermissionReply convert(Permission source);
}
