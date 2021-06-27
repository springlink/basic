package sourcefx.core.permission;

import java.util.Set;

import lombok.Value;

@Value
public class Permission {
	private final PermissionRegistry registry;

	private final String name;

	private final String title;

	public Set<Permission> getChildren() {
		return registry.getChildPermissions(name);
	}
}
