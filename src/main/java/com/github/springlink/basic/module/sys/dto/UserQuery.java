package com.github.springlink.basic.module.sys.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户查询")
public class UserQuery {
	@Schema(description = "用户名")
	private String username;

	@Schema(description = "创建时间")
	private LocalDateTime[] createdDate;
}
