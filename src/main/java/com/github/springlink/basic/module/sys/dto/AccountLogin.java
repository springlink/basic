package com.github.springlink.basic.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Account login")
public class AccountLogin {
	@NotEmpty
	@Schema(description = "Username")
	private String username;

	@NotEmpty
	@Schema(description = "Password")
	private String password;
}
