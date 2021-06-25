package sourcefx.module.sys.dto.user;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息")
public class UserReply {
	@Schema(description = "用户ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "电话号码")
	private String phoneNumber;

	@Schema(description = "Email")
	private String email;
	
	@Schema(description = "账号锁定")
	private Boolean locked;

	@Schema(description = "创建日期")
	private LocalDateTime createdDate;
}
