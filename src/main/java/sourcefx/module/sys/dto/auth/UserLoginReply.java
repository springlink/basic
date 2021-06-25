package sourcefx.module.sys.dto.auth;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录")
public class UserLoginReply {
	@Schema(description = "令牌")
	private String token;

	@Schema(description = "令牌过期时间")
	private LocalDateTime expiresAt;

	@Schema(description = "用户授权信息")
	private UserAuth auth;
}
