package com.github.springlink.basic.module.sys.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "账号登录")
public class AccountLoginReply {
	@Schema(description = "令牌")
	private String token;

	@Schema(description = "令牌过期时间")
	private LocalDateTime expiresAt;

	@Schema(description = "账号授权信息")
	private AccountAuth auth;
}
