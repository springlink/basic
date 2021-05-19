package com.github.springlink.basic.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改个人密码")
public class MySetPassword {
	@NotEmpty
	@Schema(description = "原始密码")
	private String password;

	@NotEmpty
	@Schema(description = "新密码")
	private String newPassword;
}
