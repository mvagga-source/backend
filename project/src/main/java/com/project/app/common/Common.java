package com.project.app.common;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;

public class Common {

	public static MemberDto idCheck(HttpSession session) throws BaCdException {
		MemberDto memberDto = (MemberDto) session.getAttribute("user");
		if(memberDto == null) {
			throw new BaCdException(ErrorCode.UNAUTHORIZED);	//로그인 후 사용
		}
		return memberDto;
	}
}
