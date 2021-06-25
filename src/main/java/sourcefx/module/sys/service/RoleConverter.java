package sourcefx.module.sys.service;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import sourcefx.module.sys.domain.Role;
import sourcefx.module.sys.dto.role.RoleAdd;
import sourcefx.module.sys.dto.role.RoleReply;
import sourcefx.module.sys.dto.role.RoleSetDetail;

@Mapper(componentModel = "spring")
public interface RoleConverter {
	Role addToEntity(RoleAdd add);

	RoleReply entityToReply(Role entity);

	void setDetailToEntity(RoleSetDetail setDetail, @MappingTarget Role role);
}
