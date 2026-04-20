package com.project.app.admin.service;

import java.util.List;
import java.util.Map;

import com.project.app.common.exception.BaCdException;

public interface AdminGoodsService {
	public Map<String, Object> getSummaryData() throws BaCdException;

	public Map<String, Object> updateGoodsItems(List<Map<String, Object>> updatedRows) throws BaCdException;

	public Map<String, Object> goodsList(int page, int perPage, String category, String search, String status, String stockStatus, String isBanner,
			Long minPrice, Long maxPrice, String startDate, String endDate, String sortDir) throws BaCdException;
}
