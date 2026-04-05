package com.project.app.admin.service;

import java.util.Map;

import com.project.app.common.exception.BaCdException;

public interface AdminNoticeService {

	Object ajaxList(int page, int size, String category, String search, String sortDir, String sortBy, String startDate, String endDate);

	Map<String, Object> saveAll(Map<String, Object> param) throws BaCdException;
	
	Map<String, Object> deleteAll(Map<String, Object> param) throws BaCdException;
}
