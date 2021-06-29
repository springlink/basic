package sourcefx.module.sys.domain;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sourcefx.core.data.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Setter
	private String name;

	@Setter
	private boolean disabled;

	@Setter
	@ElementCollection
	@CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
	@Column(name = "permission")
	private Set<String> permissions = Sets.newHashSet();

	public Role(String name, Set<String> permissions) {
		this.name = name;
		if (permissions != null) {
			this.permissions.addAll(permissions);			
		}
	}
}
