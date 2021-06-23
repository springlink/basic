package sourcefx.core.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "API result")
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
	private static final String CODE_OK = "OK";
	private static final String MESSAGE_OK = "Success";

	@Schema(description = "Code")
	private String code;

	@Schema(description = "Message")
	private String message;

	private T data;

	public static <T> ApiResult<T> ok(T data) {
		return new ApiResult<>(CODE_OK, MESSAGE_OK, data);
	}

	public static ApiResult<Void> ok() {
		return ok((Void) null);
	}

	public static <T> ApiResult<T> error(String code, String message, T data) {
		return new ApiResult<>(code, message, data);
	}

	public static <T> ApiResult<T> error(String code, String message) {
		return error(code, message, null);
	}
}
