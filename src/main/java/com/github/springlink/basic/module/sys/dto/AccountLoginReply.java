package com.github.springlink.basic.module.sys.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Account login reply")
public class AccountLoginReply {
	@Schema(description = "User ID")
	private String userId;

	@Schema(description = "Token")
	private String token;

	@Schema(description = "Token expires time")
	private LocalDateTime expiresAt;
}
