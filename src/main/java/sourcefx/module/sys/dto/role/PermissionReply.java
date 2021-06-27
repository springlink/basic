package sourcefx.module.sys.dto.role;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "权限信息")
public class PermissionReply {
	@Schema(description = "名称")
	private String name;

	@Schema(description = "标题")
	private String title;
	
	@JsonInclude(Include.NON_EMPTY)
	@Schema(description = "子权限")
	private List<PermissionReply> children;
}
