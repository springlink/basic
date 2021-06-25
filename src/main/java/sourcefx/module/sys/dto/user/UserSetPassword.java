package sourcefx.module.sys.dto.user;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户修改密码")
public class UserSetPassword {
	@NotNull
	@Schema(description = "用户ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@NotEmpty
	@Schema(description = "新密码")
	private String password;
}
