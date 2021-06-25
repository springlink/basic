package sourcefx.module.sys.dto.auth;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户授权信息")
public class UserAuth {
	@Schema(description = "用户ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long userId;

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "用户权限")
	private Set<String> permissions;
}
