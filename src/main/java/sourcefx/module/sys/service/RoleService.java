package sourcefx.module.sys.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;
import sourcefx.core.AppError;
import sourcefx.core.permission.PermissionRegistry;
import sourcefx.module.sys.dao.RoleRepository;
import sourcefx.module.sys.domain.QRole;
import sourcefx.module.sys.dto.role.PermissionReply;
import sourcefx.module.sys.dto.role.RoleAdd;
import sourcefx.module.sys.dto.role.RoleQuery;
import sourcefx.module.sys.dto.role.RoleReply;
import sourcefx.module.sys.dto.role.RoleSetDetail;
import sourcefx.module.sys.dto.role.RoleSetDisabled;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final PermissionRegistry permissionRegistry;
	private final RoleConverter roleConverter;
	private final RoleRepository roleRepository;

	@Transactional
	public RoleReply add(RoleAdd add) {
		return roleConverter.convert(roleRepository.save(roleConverter.convert(add)));
	}

	@Transactional
	public void setDetail(RoleSetDetail setDetail) {
		roleConverter.convert(
				setDetail,
				roleRepository.findById(setDetail.getId())
						.orElseThrow(AppError.ENTITY_NOT_FOUND::newException));
	}

	@Transactional
	public void setDisabled(RoleSetDisabled setDisabled) {
		roleRepository.findById(setDisabled.getId())
				.orElseThrow(AppError.ENTITY_NOT_FOUND::newException)
				.setDisabled(setDisabled.getDisabled());

	}

	@Transactional
	public void delete(Long id) {
		roleRepository.findById(id)
				.orElseThrow(AppError.ENTITY_NOT_FOUND::newException)
				.markDeleted();
	}

	public Page<RoleReply> page(RoleQuery query, Pageable pageable) {
		BooleanBuilder bb = new BooleanBuilder();
		if (StringUtils.hasText(query.getName())) {
			bb.and(QRole.role.name.containsIgnoreCase(query.getName()));
		}
		return roleRepository.findAll(bb, pageable).map(roleConverter::convert);
	}

	public List<PermissionReply> permissionList() {
		return permissionRegistry.getRootPermissions()
				.stream()
				.sorted((a, b) -> a.getName().compareTo(b.getName()))
				.map(roleConverter::convert)
				.collect(Collectors.toList());
	}
}
