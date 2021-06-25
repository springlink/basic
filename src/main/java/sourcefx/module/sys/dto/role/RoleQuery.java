package sourcefx.module.sys.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "查询角色")
@Data
public class RoleQuery {
	@Schema(description = "角色名称")
	private String name;
}
