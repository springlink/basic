package com.github.springlink.basic.module.sys.domain;

import java.util.Collection;
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

import com.github.springlink.basic.core.RootEntitySupport;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends RootEntitySupport {
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

	@ElementCollection
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role_id")
	private Set<String> roleIds = Sets.newHashSet();

	public User(String username, String password, String phoneNumber, String email, Collection<String> roleIds) {
		this.id = UUID.randomUUID().toString().replace("-", "");
		this.username = username;
		this.password = passwordEncoder.encode(password);
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.deleted = false;
		this.roleIds.addAll(roleIds);
	}

	public boolean passwordMatches(String raw) {
		return passwordEncoder.matches(raw, password);
	}

	public void markDeleted() {
		this.deleted = true;
	}
}
