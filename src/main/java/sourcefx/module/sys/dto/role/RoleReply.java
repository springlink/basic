package sourcefx.module.sys.dto.role;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色信息")
public class RoleReply {
	@Schema(description = "角色ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@Schema(description = "角色名称")
	private String name;

	@Schema(description = "角色权限")
	private List<String> permissions;

	@Schema(description = "是否禁用")
	private Boolean disabled;

	@Schema(description = "创建日期")
	private LocalDateTime createdDate;
}
