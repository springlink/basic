package sourcefx.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录")
public class UserLogin {
	@NotEmpty
	@Schema(description = "用户名")
	private String username;

	@NotEmpty
	@Schema(description = "密码")
	private String password;
}
