package sourcefx.module.sys.api;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sourcefx.module.sys.dto.role.PermissionReply;
import sourcefx.module.sys.dto.role.RoleAdd;
import sourcefx.module.sys.dto.role.RoleQuery;
import sourcefx.module.sys.dto.role.RoleReply;
import sourcefx.module.sys.dto.role.RoleSetDetail;
import sourcefx.module.sys.dto.role.RoleSetDisabled;
import sourcefx.module.sys.service.RoleService;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {
	private final RoleService roleService;

	@Secured("SYS_ROLE_ADD")
	@Operation(summary = "添加角色")
	@PostMapping("/add")
	public RoleReply add(@RequestBody @Valid RoleAdd body) {
		return roleService.add(body);
	}

	@Secured("SYS_ROLE_EDIT")
	@Operation(summary = "修改角色")
	@PostMapping("/setDetail")
	public void setDetail(@RequestBody @Valid RoleSetDetail body) {
		roleService.setDetail(body);
	}

	@Secured("SYS_ROLE_DELETE")
	@Operation(summary = "删除角色")
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable("id") Long id) {
		roleService.delete(id);
	}

	@Secured("SYS_ROLE_EDIT")
	@Operation(summary = "修改禁用状态")
	@PostMapping("/setDisabled")
	public void setDisabled(@RequestBody @Valid RoleSetDisabled body) {
		roleService.setDisabled(body);
	}

	@Secured("SYS_ROLE_QUERY")
	@Operation(summary = "角色列表")
	@PostMapping("/page")
	public Page<RoleReply> list(@RequestBody @Valid RoleQuery body, Pageable pageable) {
		return roleService.page(body, pageable);
	}

	@Operation(summary = "权限列表")
	@PostMapping("/permissionList")
	public Page<PermissionReply> permissionList() {
		return roleService.permissionList();
	}
}
