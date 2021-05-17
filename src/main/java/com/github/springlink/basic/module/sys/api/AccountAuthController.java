package com.github.springlink.basic.module.sys.api;

import javax.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.springlink.basic.module.sys.dto.AccountAuth;
import com.github.springlink.basic.module.sys.dto.AccountLogin;
import com.github.springlink.basic.module.sys.dto.AccountLoginReply;
import com.github.springlink.basic.module.sys.service.AccountTokenService;
import com.github.springlink.basic.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "public")
@RestController
@RequestMapping("/api/account/auth")
@RequiredArgsConstructor
public class AccountAuthController {
	private final AccountTokenService accountTokenService;

	@Operation(summary = "登录")
	@PostMapping("/login")
	public AccountLoginReply login(@RequestBody @Valid AccountLogin req) {
		return accountTokenService.login(req);
	}

	@Operation(summary = "授权信息")
	@GetMapping("/info")
	public AccountAuth info() {
		return accountTokenService.getAuthByToken(SecurityUtils.currentToken().get().getTokenValue());
	}

	@Operation(summary = "注销")
	@PostMapping("/logout")
	public void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}
