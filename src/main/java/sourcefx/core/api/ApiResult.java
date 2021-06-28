package sourcefx.core.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "API调用结果")
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
	private static final String CODE_OK = "OK";
	private static final String MESSAGE_OK = "Success";

	@Schema(description = "错误码")
	private String code;

	@Schema(description = "错误信息")
	private String message;

	private T data;

	public static <T> ApiResult<T> ok(T data) {
		return new ApiResult<>(CODE_OK, MESSAGE_OK, data);
	}

	public static ApiResult<Void> ok() {
		return ok((Void) null);
	}

	public static <T> ApiResult<Page<T>> ok(org.springframework.data.domain.Page<T> page) {
		return ok(new Page<>(page.getContent(), page.getTotalElements()));
	}

	public static <T> ApiResult<T> error(String code, String message, T data) {
		return new ApiResult<>(code, message, data);
	}

	public static <T> ApiResult<T> error(String code, String message) {
		return error(code, message, null);
	}

	@Data
	@Schema(description = "分页数据")
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Page<T> {
		@Schema(description = "数据列表")
		private List<T> elements;

		@Schema(description = "数据总数")
		private Long total;
	}
}
