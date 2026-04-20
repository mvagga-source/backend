package com.project.app.admin.service;

import java.util.List;
import java.util.Map;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.exception.BaCdException;

public interface AdminGoodsSettlementService {
	public Map<String, Object> updateSettlementStatus(List<Map<String, Object>> updatedRows) throws BaCdException;

	public Map<String, Object> list(int page, int perPage, int minPrice, int maxPrice, String settleYn, String delivStatus,
            String category, String status, String search, String sortDir,
            String sortBy, String startDate, String endDate,
			Long minAmount, Long maxAmount) throws BaCdException;

	public Map<String, Object> settleOrders(List<Long> orderIds, MemberDto admin) throws BaCdException;
}
