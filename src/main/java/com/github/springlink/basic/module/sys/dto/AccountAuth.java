package com.github.springlink.basic.module.sys.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "账号授权信息")
public class AccountAuth {
	@Schema(description = "用户ID")
	private String userId;

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "账号权限")
	private Set<String> permissions;
}
