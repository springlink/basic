package com.github.springlink.basic.api;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.springlink.basic.module.sys.dto.AccountAdd;
import com.github.springlink.basic.module.sys.dto.AccountChangeProfile;
import com.github.springlink.basic.module.sys.dto.AccountReply;
import com.github.springlink.basic.module.sys.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "account")
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountApi {
	private final AccountService accountService;

	@Operation(summary = "Add account")
	@PostMapping("/add")
	public AccountReply add(@RequestBody @Valid AccountAdd body) {
		return accountService.add(body);
	}

	@Operation(summary = "Change profile")
	@PostMapping("/changeProfile")
	public void changeProfile(@RequestBody @Valid AccountChangeProfile body) {
		accountService.changeProfile(body);
	}

	@Operation(summary = "Delete account")
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable("id") String id) {
		accountService.delete(id);
	}

	@Operation(summary = "List accounts")
	@GetMapping("/page")
	public Page<AccountReply> list(Pageable pageable) {
		return accountService.page(pageable);
	}
}
