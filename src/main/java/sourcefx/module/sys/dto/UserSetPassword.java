package sourcefx.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户修改密码")
public class UserSetPassword {
	@NotEmpty
	@Schema(description = "用户ID")
	private String id;

	@NotEmpty
	@Schema(description = "新密码")
	private String password;
}
