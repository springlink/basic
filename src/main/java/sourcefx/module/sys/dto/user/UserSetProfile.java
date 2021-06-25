package sourcefx.module.sys.dto.user;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户修改资料")
public class UserSetProfile {
	@NotNull
	@Schema(description = "用户ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@Schema(description = "电话号码")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;

	@Schema(description = "头像")
	private String avatar;
}
