package sourcefx.module.sys.api;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sourcefx.module.sys.dto.UserAdd;
import sourcefx.module.sys.dto.UserQuery;
import sourcefx.module.sys.dto.UserReply;
import sourcefx.module.sys.dto.UserSetLocked;
import sourcefx.module.sys.dto.UserSetPassword;
import sourcefx.module.sys.dto.UserSetProfile;
import sourcefx.module.sys.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@Operation(summary = "添加用户")
	@PostMapping("/add")
	public UserReply add(@RequestBody @Valid UserAdd body) {
		return userService.add(body);
	}

	@Operation(summary = "修改资料")
	@PostMapping("/setProfile")
	public void setProfile(@RequestBody @Valid UserSetProfile body) {
		userService.setProfile(body);
	}

	@Operation(summary = "修改密码")
	@PostMapping("/setPassword")
	public void setPassword(@RequestBody @Valid UserSetPassword body) {
		userService.setPassword(body);
	}

	@Operation(summary = "修改锁定状态")
	@PostMapping("/setLocked")
	public void setLocked(@RequestBody @Valid UserSetLocked body) {
		userService.setLocked(body);
	}

	@Operation(summary = "删除用户")
	@DeleteMapping("/remove/{id}")
	public void remove(@PathVariable("id") String id) {
		userService.delete(id);
	}

	@Operation(summary = "用户列表")
	@PostMapping("/page")
	public Page<UserReply> list(@RequestBody @Valid UserQuery body, Pageable pageable) {
		return userService.page(body, pageable);
	}
}
