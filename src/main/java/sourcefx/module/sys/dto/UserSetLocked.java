package sourcefx.module.sys.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设置用户锁定状态")
public class UserSetLocked {
	@NotEmpty
	@Schema(description = "用户ID")
	private String id;

	@Schema(description = "锁定状态")
	private Boolean locked;
}
