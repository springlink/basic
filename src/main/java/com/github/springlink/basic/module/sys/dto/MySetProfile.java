package com.github.springlink.basic.module.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改个人资料")
public class MySetProfile {
	@Schema(description = "电话号码")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;

	@Schema(description = "头像")
	private String avatar;
}
