package sourcefx.module.sys.dto.role;

import java.util.Set;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "新增角色")
@Data
public class RoleAdd {
	@NotEmpty
	@Schema(description = "角色名称")
	private String name;

	@Schema(description = "角色权限")
	private Set<String> permissions;
}
