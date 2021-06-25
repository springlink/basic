package sourcefx.module.sys.api;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sourcefx.module.sys.dto.user.UserAdd;
import sourcefx.module.sys.dto.user.UserQuery;
import sourcefx.module.sys.dto.user.UserReply;
import sourcefx.module.sys.dto.user.UserSetLocked;
import sourcefx.module.sys.dto.user.UserSetPassword;
import sourcefx.module.sys.dto.user.UserSetProfile;
import sourcefx.module.sys.service.UserService;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@Secured("SYS_USER_ADD")
	@Operation(summary = "添加用户")
	@PostMapping("/add")
	public UserReply add(@RequestBody @Valid UserAdd body) {
		return userService.add(body);
	}

	@Secured("SYS_USER_SET_PROFILE")
	@Operation(summary = "修改资料")
	@PostMapping("/setProfile")
	public void setProfile(@RequestBody @Valid UserSetProfile body) {
		userService.setProfile(body);
	}

	@Secured("SYS_ROLE_SET_PASSWORD")
	@Operation(summary = "修改密码")
	@PostMapping("/setPassword")
	public void setPassword(@RequestBody @Valid UserSetPassword body) {
		userService.setPassword(body);
	}

	@Secured("SYS_ROLE_SET_LOCKED")
	@Operation(summary = "修改锁定状态")
	@PostMapping("/setLocked")
	public void setLocked(@RequestBody @Valid UserSetLocked body) {
		userService.setLocked(body);
	}

	@Secured("SYS_ROLE_DELETE")
	@Operation(summary = "删除用户")
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable("id") Long id) {
		userService.delete(id);
	}

	@Secured("SYS_ROLE_QUERY")
	@Operation(summary = "用户列表")
	@PostMapping("/page")
	public Page<UserReply> list(@RequestBody @Valid UserQuery body, Pageable pageable) {
		return userService.page(body, pageable);
	}
}
