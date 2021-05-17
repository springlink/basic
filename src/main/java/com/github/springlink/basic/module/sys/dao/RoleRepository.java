package com.github.springlink.basic.module.sys.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.springlink.basic.module.sys.domain.Role;

public interface RoleRepository extends PagingAndSortingRepository<Role, String> {
	List<Role> findAllByIdInAndDeletedFalse(Collection<String> ids);
}
