package com.no.hurdles.spray.result;

public class BaseResult {

	public static final BaseResult SUCCESS;
	public static final BaseResult FAIL;
	private Boolean success;
	private String message;

	static {
		SUCCESS = new BaseResult(true,"success");
		FAIL = new BaseResult(false, "error");
	}

	private BaseResult(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public static BaseResult fail( String message) {
		return new BaseResult(false, message);
	}

	public Boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}
}