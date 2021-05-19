package com.github.springlink.basic.core;

import lombok.Getter;

public class ApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Getter
	private final String code;

	public ApiException(String code) {
		this.code = code;
	}

	public ApiException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ApiException(String code, String message) {
		super(message);
		this.code = code;
	}

	public ApiException(String code, Throwable cause) {
		super(cause);
		this.code = code;
	}
}
