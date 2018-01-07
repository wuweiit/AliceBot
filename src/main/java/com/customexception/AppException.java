package com.customexception;

public class AppException extends RuntimeException {
	/**
	 * 将上级无法处理的异常都转换成运行时异常。
	 */
	private static final long serialVersionUID = 1L;

	public AppException() {
	}

	public AppException(String name) {
		super(name);
	}

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(String name, Throwable cause) {
		super(name, cause);
	}
}
