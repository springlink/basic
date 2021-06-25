package sourcefx.core.permission;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class PermissionRegistry {
	private final ConcurrentMap<String, Permission> permissions = Maps.newConcurrentMap();

	public PermissionRegistry register(String name, String title, String description, String... priors) {
		permissions.computeIfAbsent(name, k -> {
			return new Permission(name, title, description);
		});
	}
}
