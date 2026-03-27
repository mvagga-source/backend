package com.project.app.common.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.errorcode.ErrorResponse;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaCdException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaCdException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status;
        // 로그인/토큰 관련 에러는 401
        /*if (e.getErrorCode() == ErrorCode.UNAUTHORIZED) {		//시큐리티 토큰설정 후 이용
            status = HttpStatus.UNAUTHORIZED;
        } else {*/
        	status = HttpStatus.OK; // 예시: 필요에 따라 커스터마이징 가능
        //}
        e.printStackTrace();
        // 클라이언트로 JSON 반환
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(errorCode, e.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handle404(NoHandlerFoundException e) throws IOException {
    	e.printStackTrace();
    	ErrorCode errorCode = ErrorCode.PAGE_NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.NOT_FOUND);
    }

    /*@ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
        // 404 Not Found 반환
    	e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청하신 리소스를 찾을 수 없습니다.");
    }*/

    @ExceptionHandler(NoResourceFoundException.class)	//스프링부트 3.2부터 NoResourceFoundException로 404대신 500에러로 발생 -> 정적 리소스(CSS, JS, 이미지)나 API 엔드포인트를 찾지 못했을 때 404 Not Found를 처리하는 방식이 변경
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        // 404 Not Found 반환
    	e.printStackTrace();
    	ErrorCode errorCode = ErrorCode.PAGE_NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.NOT_FOUND);
    }

    // 기타 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
    	e.printStackTrace();
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //ajax용도로 추가(model)에 담아서 보내는건 안 만듬
}
