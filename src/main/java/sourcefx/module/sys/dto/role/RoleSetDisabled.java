package sourcefx.module.sys.dto.role;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设置角色是否禁用")
public class RoleSetDisabled {
	@NotNull
	@Schema(description = "角色ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@NotNull
	@Schema(description = "是否禁用")
	private Boolean disabled;
}
