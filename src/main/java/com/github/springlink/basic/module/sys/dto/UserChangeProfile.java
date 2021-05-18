package com.github.springlink.basic.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户修改资料")
public class UserChangeProfile {
	@NotEmpty
	@Schema(description = "用户ID")
	private String id;

	@Schema(description = "电话号码")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;
}
