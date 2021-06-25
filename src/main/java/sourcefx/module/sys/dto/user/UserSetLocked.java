package sourcefx.module.sys.dto.user;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设置用户锁定状态")
public class UserSetLocked {
	@NotNull
	@Schema(description = "用户ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@Schema(description = "锁定状态")
	private Boolean locked;
}
