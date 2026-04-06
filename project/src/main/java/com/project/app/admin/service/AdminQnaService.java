package com.project.app.admin.service;

import java.util.Map;

import com.project.app.common.exception.BaCdException;
import com.project.app.qna.dto.QnaDto;

public interface AdminQnaService {

	Map<String, Object> ajaxList(int page, int size, String category, String search, String status, String startDate,
			String endDate) throws BaCdException;

	Map<String, Object> saveReply(Map<String, Object> param) throws BaCdException;

	Map<String, Object> deleteAll(Map<String, Object> param) throws BaCdException;
	
	QnaDto view(Long qno) throws BaCdException;

}
