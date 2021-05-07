package com.github.springlink.basic.core;

import lombok.Getter;

public class ApplicationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Getter
	private final String code;

	public ApplicationException(String code) {
		this.code = code;
	}

	public ApplicationException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ApplicationException(String code, String message) {
		super(message);
		this.code = code;
	}

	public ApplicationException(String code, Throwable cause) {
		super(cause);
		this.code = code;
	}
}
