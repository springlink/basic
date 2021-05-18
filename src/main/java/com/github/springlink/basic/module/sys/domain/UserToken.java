package com.github.springlink.basic.module.sys.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.github.springlink.basic.core.RootEntitySupport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserToken extends RootEntitySupport {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String userId;
	
	@Lob
	private String data;

	private LocalDateTime expiresAt;

	public UserToken(String userId, Duration ttl, String data) {
		this.id = UUID.randomUUID().toString().replace("-", "");
		this.userId = userId;
		this.expiresAt = LocalDateTime.now().plus(ttl);
		this.data = data;
	}
	
	public boolean isValidNow() {
		return !LocalDateTime.now().isAfter(expiresAt);
	}
}
