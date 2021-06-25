package sourcefx.module.sys.dto.role;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改角色信息")
public class RoleSetDetail {
	@NotNull
	@Schema(description = "角色ID")
	private Long id;

	@Schema(description = "角色名称")
	private String name;

	@Schema(description = "角色权限")
	private List<String> permissions;
}
