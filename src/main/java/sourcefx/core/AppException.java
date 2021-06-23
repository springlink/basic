package sourcefx.core;

import lombok.Getter;

public class AppException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Getter
	private final String code;

	public AppException(String code) {
		this.code = code;
	}

	public AppException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public AppException(String code, String message) {
		super(message);
		this.code = code;
	}

	public AppException(String code, Throwable cause) {
		super(cause);
		this.code = code;
	}
}
