package sourcefx.module.sys.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Lob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sourcefx.core.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserToken extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private String token;

	private Long userId;

	@Lob
	private String data;

	private LocalDateTime expiresAt;

	public UserToken(Long userId, Duration ttl, String data) {
		this.token = UUID.randomUUID().toString().replace("-", "");
		this.userId = userId;
		this.expiresAt = LocalDateTime.now().plus(ttl);
		this.data = data;
	}

	public boolean isValidNow() {
		return !LocalDateTime.now().isAfter(expiresAt);
	}

	public void touch(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
}
