package com.github.springlink.basic.module.sys.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.github.springlink.basic.module.sys.domain.Account;

public interface AccountRepository extends PagingAndSortingRepository<Account, String> {
	Optional<Account> findByUsername(String username);

	Optional<Account> findByUsernameAndDeletedFalse(String username);

	Optional<Account> findByIdAndDeletedFalse(String id);

	Page<Account> findAllByDeletedFalse(Pageable pageable);
}
