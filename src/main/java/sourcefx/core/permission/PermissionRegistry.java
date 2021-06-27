package sourcefx.core.permission;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Component
public class PermissionRegistry implements BeanPostProcessor {
	private final Map<String, Permission> permissionMap = Maps.newConcurrentMap();
	private final Map<String, Set<Permission>> childrenMap = Maps.newConcurrentMap();
	private final Set<Permission> rootSet = Sets.newHashSet();

	public Builder root(String name, String title) {
		return new Builder(null, name, title);
	}

	public Set<Permission> getAllPermissions() {
		return Collections.unmodifiableSet(Sets.newHashSet(permissionMap.values()));
	}

	public Set<Permission> getRootPermissions() {
		return Collections.unmodifiableSet(rootSet);
	}

	public Set<Permission> getChildPermissions(String parent) {
		if (!permissionMap.containsKey(parent)) {
			throw new IllegalArgumentException("Permission [" + parent + "] doesn't registered");
		}
		Set<Permission> children = childrenMap.get(parent);
		return children != null ? Collections.unmodifiableSet(children) : Collections.emptySet();
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof PermissionRegistrar) {
			((PermissionRegistrar) bean).register(this);
		}
		return bean;
	}

	public class Builder {
		private final Permission permission;

		private final Builder parent;

		private Builder(Builder parent, String name, String title) {
			this.parent = parent;
			this.permission = new Permission(PermissionRegistry.this, name, title);
		}

		public Builder child(String name, String title) {
			return new Builder(this, permission.getName() + ":" + name, title);
		}

		public Builder add() {
			if (permissionMap.containsKey(permission.getName())) {
				throw new IllegalStateException("Permission [" + permission.getName() + "] already exists");
			}
			permissionMap.put(permission.getName(), permission);
			if (parent != null) {
				childrenMap.computeIfAbsent(parent.permission.getName(), k -> Sets.newHashSet())
						.add(permission);
			} else {
				rootSet.add(permission);
			}
			return parent;
		}
	}
}
