package com.project.app.common.exception;

import org.springframework.http.HttpStatus;

import com.project.app.common.errorcode.ErrorCode;

import lombok.Getter;

@Getter
public class BaCdException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final ErrorCode errorCode;
	//private final HttpStatus status;
	private final boolean success=false;

    // 기본 생성자
    public BaCdException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 메시지 커스터마이징 가능 (오버로딩)
    public BaCdException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() { return errorCode; }
}
