package com.github.springlink.basic.module.sys.domain;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.github.springlink.basic.core.RootEntitySupport;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends RootEntitySupport {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String name;

	private boolean deleted;

	@ElementCollection
	@CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
	@Column(name = "permission")
	private Set<String> permissions = Sets.newHashSet();

	public Role(String name, Set<String> permissions) {
		this.id = UUID.randomUUID().toString().replace("-", "");
		this.name = name;
		this.permissions.addAll(permissions);
	}

	public void markDeleted() {
		this.deleted = true;
	}
}
