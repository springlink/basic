package sourcefx.module.sys.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "权限信息")
public class PermissionReply {
	@Schema(description = "权限名称")
	private String name;

	@Schema(description = "权限标题")
	private String title;

	@Schema(description = "权限描述")
	private String description;
}
