package com.project.app.common.errorcode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
	// 공통 에러
	//코드값 기준은 세세하게 하기보단 Empty인지 올바른 입력값인지로 구분하고 메세지를 따로 코드값이랑 매개변수로 "..."사용해도 됨 > 깃허브 여기에 작성시 파일 중복주의
    INVALID_INPUT_VALUE("001", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED("002", "지원하지 않는 HTTP 메서드입니다."),
    USER_NOT_FOUND("101", "사용자를 찾을 수 없습니다."),
    USERID_INVALID("102", "아이디는 영문 소문자와 숫자 조합 4~16자리로 입력해주세요."),
    USERID_DUPLICATE("103", "이미 사용 중인 아이디입니다."),
    NICKNAME_DUPLICATE("104", "이미 사용 중인 닉네임입니다."),
    PASSWORD_EMPTY("111", "비밀번호를 입력해주세요."),
    PASSWORD_WEAK("112", "비밀번호가 정책에 맞지 않습니다. (영문, 숫자, 특수문자 포함 10~16자리 이상)"),
    PASSWORD_MISMATCH("113", "비밀번호가 일치하지 않습니다."),
    EMAIL_EMPTY("121", "이메일을 입력해 주세요."),
    DUPLICATE_EMAIL("122", "이미 사용 중인 이메일입니다."),
    USER_NOT_MATCH("123", "사용자와 일치하지 않습니다."),
    AUTH_USER_NOT_MATCH("124", "작성자가 아닙니다."),
    AUTH_USER_NOT_ORDER("125", "본인이 구매한 주문 내역만 접근 가능합니다."),
    AUTH_USER_NOT_ORDER_SAVE("126", "본인이 구매한 주문 내역만 리뷰등록이 가능합니다."),
    AUTH_ADMIN_NOT_FOUND("127", "관리자만 접근가능합니다."),
    AUTH_USER_NOT_SALER("128", "판매자가 아닙니다."),
    AUTH_USER_NOT_ORDER_RETURN("129", "본인이 반품한 주문 내역만 접근 가능합니다."),

    INPUT_EMPTY("201","필수입력값입니다."),
    NOT_FOUND("202", "데이터가 없습니다."),
    INVALID_PARAMETER("203", "잘못된 상태값입니다."),
    COMPLETE_STATUS("204", "이미 완료된 상태입니다."),
    NOT_MATCH("205", "일치하지 않습니다."),
    IS_EXIST("206", "이미 존재합니다."),
	PAGE_EMPTY("207","존재하지 않는 페이지입니다."),
	PARAMETER_PAGE_TYPE_MISMATCH("208", "잘못된 파라미터 형식입니다."),
	NOTIFICATION_NOT_FOUND("209","알림이 존재하지 않습니다."),
	IS_STATUS("210","이미 처리중인 상태입니다."),

    LOGOUT_SUCCESS("150", "로그아웃 되었습니다."),
    UNAUTHORIZED("401", "로그인을 진행해 주세요."),
    FORBIDDEN_ERROR("403", "접근 권한이 없습니다."),
    PAGE_NOT_FOUND("404", "페이지를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR("500", "서버 오류가 발생했습니다. 지속시 문의 부탁드립니다."),
	
	KAKAO_PAY_APPROVE_ERROR("600", "카카오페이 승인 중 오류 발생"),
	KAKAO_PAY_EMPTY_STOCKCNT("601", "결제 도중 재고가 소진되었습니다.");

    private final String code;
    private final String message;
}
