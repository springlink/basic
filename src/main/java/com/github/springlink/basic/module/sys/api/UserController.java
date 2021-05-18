package com.github.springlink.basic.module.sys.api;

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

import com.github.springlink.basic.module.sys.dto.UserAdd;
import com.github.springlink.basic.module.sys.dto.UserChangeProfile;
import com.github.springlink.basic.module.sys.dto.UserReply;
import com.github.springlink.basic.module.sys.service.UserService;

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
	@PostMapping("/changeProfile")
	public void changeProfile(@RequestBody @Valid UserChangeProfile body) {
		userService.changeProfile(body);
	}

	@Operation(summary = "删除用户")
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable("id") String id) {
		userService.delete(id);
	}

	@Operation(summary = "用户列表")
	@GetMapping("/page")
	public Page<UserReply> list(Pageable pageable) {
		return userService.page(pageable);
	}
}
