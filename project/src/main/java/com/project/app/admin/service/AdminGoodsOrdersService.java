package com.project.app.admin.service;

import java.util.List;
import java.util.Map;

import com.project.app.common.exception.BaCdException;

public interface AdminGoodsOrdersService {
	public Map<String, Object> list(int page, int size, int minPrice, int maxPrice, String settleYn, String delivStatus,
            String category, String status, String search, String sortDir, 
            String sortBy, String startDate, String endDate) throws BaCdException;
	
	public Map<String, Object> updateOrders(List<Map<String, Object>> updatedRows) throws BaCdException;
}
