package com.github.springlink.basic.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Account info to be add")
public class AccountAdd {
	@NotEmpty
	@Schema(description = "Username")
	private String username;

	@NotEmpty
	@Schema(description = "Password")
	private String password;

	@Schema(description = "Phone number")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;
}
