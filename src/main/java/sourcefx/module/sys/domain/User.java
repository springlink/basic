package sourcefx.module.sys.domain;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sourcefx.core.AppException;
import sourcefx.core.AppUtils;
import sourcefx.core.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Transient
	private final PasswordEncoder passwordEncoder = AppUtils.getInstance().getBean(PasswordEncoder.class);

	@Id
	private String id;

	private String username;

	private String password;

	@Setter
	private String phoneNumber;

	@Setter
	private String email;

	@Setter
	private String avatar;

	private boolean builtIn;

	@Setter
	private boolean locked;

	private boolean deleted;

	@ElementCollection
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role_id")
	private Set<String> roleIds = Sets.newHashSet();

	public User(String username, String password, boolean builtIn) {
		this.id = UUID.randomUUID().toString().replace("-", "");
		this.username = username;
		this.password = passwordEncoder.encode(password);
		this.builtIn = builtIn;
		this.locked = false;
		this.deleted = false;
	}

	public void setPassword(String password) {
		this.password = passwordEncoder.encode(password);
	}

	public boolean passwordMatches(String raw) {
		return passwordEncoder.matches(raw, password);
	}

	public void delete() {
		if (builtIn) {
			throw new AppException("DELETE_ON_BUILT_IN_USER");
		}
		this.deleted = true;
	}
}
