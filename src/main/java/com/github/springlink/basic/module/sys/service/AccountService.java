package com.github.springlink.basic.module.sys.service;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.springlink.basic.core.ApplicationException;
import com.github.springlink.basic.module.sys.domain.Account;
import com.github.springlink.basic.module.sys.dto.AccountAdd;
import com.github.springlink.basic.module.sys.dto.AccountChangeProfile;
import com.github.springlink.basic.module.sys.dto.AccountReply;
import com.github.springlink.basic.module.sys.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountMapper accountMapper;
	private final AccountRepository accountRepository;

	@Transactional
	@EventListener
	public void onContextRefreshed(ContextRefreshedEvent event) {
		if (!accountRepository.findByUsernameAndDeletedFalse("root").isPresent()) {
			Account account = new Account("root", "123456", null, null);
			accountRepository.save(account);
		}
	}

	@Transactional
	public AccountReply add(AccountAdd aa) {
		return accountMapper.entityToReply(accountRepository.save(accountMapper.addToEntity(aa)));
	}

	@Transactional
	public void changeProfile(AccountChangeProfile apc) {
		Account account = accountRepository.findByIdAndDeletedFalse(apc.getId())
				.orElseThrow(() -> new ApplicationException("OBJECT_NOT_FOUND"));
		accountMapper.changeProfileToEntity(apc, account);
	}

	@Transactional
	public void delete(String id) {
		Account account = accountRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new ApplicationException("OBJECT_NOT_FOUND"));
		account.markDeleted();
	}

	public Page<AccountReply> page(Pageable pageable) {
		return accountRepository.findAllByDeletedFalse(pageable).map(accountMapper::entityToReply);
	}
}
