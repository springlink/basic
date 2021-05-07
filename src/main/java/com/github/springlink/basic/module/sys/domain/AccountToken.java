package com.github.springlink.basic.module.sys.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.github.springlink.basic.core.RootEntitySupport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountToken extends RootEntitySupport {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String userId;

	private LocalDateTime expiresAt;

	public AccountToken(String userId, Duration ttl) {
		this.id = UUID.randomUUID().toString().replace("-", "");
		this.userId = userId;
		this.expiresAt = LocalDateTime.now().plus(ttl);
	}
	
	public boolean isValidNow() {
		return !LocalDateTime.now().isAfter(expiresAt);
	}
}
