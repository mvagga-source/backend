package com.project.app.admin.service;

import java.util.Map;

import com.project.app.common.exception.BaCdException;
import com.project.app.idea.dto.IdeaDto;

public interface AdminIdeaService {

	public Map<String, Object> ajaxList(int page, int perPage, String category, String ideacategory, String search, String status, String startDate,
			String endDate) throws BaCdException;
	
	IdeaDto view(Long ideano); // 단건 조회
	
    Map<String, Object> saveFeedback(Map<String, Object> param); // 의견 저장
}
