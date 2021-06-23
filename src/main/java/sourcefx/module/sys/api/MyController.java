package sourcefx.module.sys.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sourcefx.module.sys.dto.MyProfileReply;
import sourcefx.module.sys.dto.MySetPassword;
import sourcefx.module.sys.dto.MySetProfile;
import sourcefx.module.sys.service.MyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "个人信息")
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyController {
	private final MyService myService;

	@Operation(summary = "修改资料")
	@GetMapping("/profile")
	public MyProfileReply profile() {
		return myService.profile();
	}

	@Operation(summary = "修改资料")
	@PostMapping("/setProfile")
	public void setProfile(@RequestBody @Valid MySetProfile body) {
		myService.setProfile(body);
	}

	@Operation(summary = "修改密码")
	@PostMapping("/setPassword")
	public void setPassword(@RequestBody @Valid MySetPassword body) {
		myService.setPassword(body);
	}
}
