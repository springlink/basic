package sourcefx.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "添加用户")
public class UserAdd {
	@NotEmpty
	@Schema(description = "用户名")
	private String username;

	@NotEmpty
	@Schema(description = "密码")
	private String password;

	@Schema(description = "电话号码")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;

	@Schema(description = "头像")
	private String avatar;
}
