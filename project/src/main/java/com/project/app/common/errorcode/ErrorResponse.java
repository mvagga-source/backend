package com.project.app.common.errorcode;

import lombok.Getter;

@Getter
public class ErrorResponse {
	//ajax용도로 추가(model)에 담아서 보내는건 안 만듬
    private final boolean success = false;
    private final String code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
    	this.code = errorCode.getCode();
        this.message = message;
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}