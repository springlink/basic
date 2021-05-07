package com.github.springlink.basic.module.sys.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Account reply")
public class AccountReply {
	@Schema(description = "Account ID")
	private String id;

	@Schema(description = "Username")
	private String username;

	@Schema(description = "Phone number")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;

	@Schema(description = "Created date")
	private LocalDateTime createdDate;
}
