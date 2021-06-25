package sourcefx.module.sys.api;

import javax.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sourcefx.core.AppUtils;
import sourcefx.module.sys.dto.auth.UserAuth;
import sourcefx.module.sys.dto.auth.UserLogin;
import sourcefx.module.sys.dto.auth.UserLoginReply;
import sourcefx.module.sys.service.UserTokenService;

@Tag(name = "用户授权")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AppUtils appUtils;
	private final UserTokenService tokenService;

	@Operation(summary = "登录")
	@PostMapping("/login")
	public UserLoginReply login(@RequestBody @Valid UserLogin body) {
		return tokenService.login(body);
	}

	@Operation(summary = "授权信息")
	@GetMapping("/info")
	public UserAuth info() {
		return tokenService.getAuthByToken(appUtils.getCurrentToken().get().getTokenValue());
	}

	@Operation(summary = "注销")
	@PostMapping("/logout")
	public void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}
