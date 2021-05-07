package com.github.springlink.basic.module.sys.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.springlink.basic.core.RootEntitySupport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends RootEntitySupport {
	private static final long serialVersionUID = 1L;

	@Transient
	private final PasswordEncoder passwordEncoder = getBean(PasswordEncoder.class);

	@Id
	private String id;

	private String username;

	private String password;

	private String phoneNumber;

	private String email;
	
	private Boolean deleted;

	public Account(String username, String password, String phoneNumber, String email) {
		this.id = UUID.randomUUID().toString().replace("-", "");
		this.username = username;
		this.password = passwordEncoder.encode(password);
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.deleted = false;
	}

	public boolean passwordMatches(String raw) {
		return passwordEncoder.matches(raw, password);
	}
	
	public void markDeleted() {
		this.deleted = true;
	}
}
