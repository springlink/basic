package sourcefx.module.sys.constant;

import org.springframework.stereotype.Component;

import sourcefx.core.permission.PermissionRegistrar;
import sourcefx.core.permission.PermissionRegistry;

@Component
public class SysPermissionRegistrar implements PermissionRegistrar {
	@Override
	public void register(PermissionRegistry registry) {
		registry.root("sys.user", "用户管理")
				.child("add", "新增").add()
				.child("setProfile", "修改资料").add()
				.child("setPassword", "修改密码").add()
				.child("setLocked", "修改锁定状态").add()
				.child("delete", "删除").add()
				.add();

		registry.root("sys.role", "角色管理")
				.child("add", "新增").add()
				.child("edit", "修改").add()
				.child("delete", "删除").add()
				.add();
	}
}
