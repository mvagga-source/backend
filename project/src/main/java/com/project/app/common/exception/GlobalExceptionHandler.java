package com.project.app.common.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.errorcode.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
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

    /*@ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handle404(NoHandlerFoundException e) throws IOException {
    	e.printStackTrace();
    	ErrorCode errorCode = ErrorCode.PAGE_NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.NOT_FOUND);
    }*/
    
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public Object handle404(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        
        String accept = request.getHeader("Accept");
        
        // API 요청(JSON)인 경우
        if (accept != null && accept.contains("application/json")) {
            ErrorCode errorCode = ErrorCode.PAGE_NOT_FOUND;
            return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.NOT_FOUND);
        } 
        
        // 일반 브라우저 주소창 접근인 경우
        ModelAndView mav = new ModelAndView();
        mav.setViewName("admin/error/404"); // /WEB-INF/views/admin/error/404.jsp
        return mav;
    }

    /*@ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
        // 404 Not Found 반환
    	e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("요청하신 리소스를 찾을 수 없습니다.");
    }*/

    /*@ExceptionHandler(NoResourceFoundException.class)	//스프링부트 3.2부터 NoResourceFoundException로 404대신 500에러로 발생 -> 정적 리소스(CSS, JS, 이미지)나 API 엔드포인트를 찾지 못했을 때 404 Not Found를 처리하는 방식이 변경
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        // 404 Not Found 반환
    	e.printStackTrace();
    	ErrorCode errorCode = ErrorCode.PAGE_NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.NOT_FOUND);
    }*/
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        // 1. 로그 기록
        e.printStackTrace(); 

        // 2. 에러 코드 설정
        ErrorCode errorCode = ErrorCode.PARAMETER_PAGE_TYPE_MISMATCH;

        // 프론트 처리를 위해 HttpStatus.OK(200)
        return ResponseEntity
                .status(HttpStatus.OK) 
                .body(new ErrorResponse(errorCode, "잘못된 파라미터 형식입니다."));
    }

    // 기타 Exception 처리
    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
    	e.printStackTrace();
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.INTERNAL_SERVER_ERROR);
    }*/
    
	// 기타 Exception 처리
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        e.printStackTrace();

        // AJAX 요청인지 확인 (리액트에서 보낸 요청인지)
        String header = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(header) || request.getHeader("Accept").contains("application/json");

        if (isAjax) {
            // 1. 리액트(JSON) 응답
            ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(errorCode, e.getMessage()));
        } else {
            // 2. JSP(View) 응답
            // 에러 메시지를 담아서 에러 전용 JSP 페이지로 이동
            ModelAndView mav = new ModelAndView();
            mav.addObject("msg", e.getMessage());
            mav.setViewName("admin/error/500"); // /WEB-INF/views/admin/common/error.jsp
            return mav;
        }
    }
    
    // 기타 Exception 처리
    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e, jakarta.servlet.http.HttpServletRequest request) {
        // SSE 요청인지 확인 (Accept 헤더가 text/event-stream 인지 체크)
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("text/event-stream")) {
            // SSE 요청 중 에러 발생 시 로그만 찍고 빈 응답을 보냄
            System.out.println(">>> SSE Connection Error: " + e.getMessage());		//SSE에러
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        e.printStackTrace();
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.INTERNAL_SERVER_ERROR);
    }*/

    //ajax용도로 추가(model)에 담아서 보내는건 안 만듬
}
