package com.github.springlink.basic.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.springlink.basic.module.sys.dto.AccountLogin;
import com.github.springlink.basic.module.sys.dto.AccountLoginReply;
import com.github.springlink.basic.module.sys.service.AccountTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "public")
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicApi {
	private final AccountTokenService accountTokenService;

	@Operation(summary = "Account login")
	@PostMapping("/login")
	public AccountLoginReply login(@RequestBody @Valid AccountLogin req) {
		return accountTokenService.login(req);
	}
}
