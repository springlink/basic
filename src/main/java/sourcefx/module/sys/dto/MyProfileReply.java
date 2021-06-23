package sourcefx.module.sys.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人资料")
public class MyProfileReply {
	@Schema(description = "用户ID")
	private String id;

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "电话号码")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;

	@Schema(description = "创建日期")
	private LocalDateTime createdDate;
}
