package com.github.springlink.basic.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Account change profile")
public class AccountChangeProfile {
	@NotEmpty
	@Schema(description = "Account ID")
	private String id;

	@Schema(description = "Phone number")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;
}
