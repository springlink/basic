package com.github.springlink.basic.module.sys.api;

import javax.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.springlink.basic.module.sys.dto.UserAuth;
import com.github.springlink.basic.module.sys.dto.UserLogin;
import com.github.springlink.basic.module.sys.dto.UserLoginReply;
import com.github.springlink.basic.module.sys.service.TokenService;
import com.github.springlink.basic.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "用户授权")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final TokenService tokenService;

	@Operation(summary = "登录")
	@PostMapping("/login")
	public UserLoginReply login(@RequestBody @Valid UserLogin body) {
		return tokenService.login(body);
	}

	@Operation(summary = "授权信息")
	@GetMapping("/info")
	public UserAuth info() {
		return tokenService.getAuthByToken(SecurityUtils.getCurrentToken().get().getTokenValue());
	}

	@Operation(summary = "注销")
	@PostMapping("/logout")
	public void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}
