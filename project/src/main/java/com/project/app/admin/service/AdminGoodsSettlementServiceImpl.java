package com.project.app.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminGoodsRepository;
import com.project.app.admin.repository.AdminGoodsReviewRepository;
import com.project.app.admin.repository.AdminGoodsSettlementRepository;
import com.project.app.common.GridUtils;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsSettlement.dto.GoodsSettlementDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminGoodsSettlementServiceImpl implements AdminGoodsSettlementService {
	private final AdminGoodsSettlementRepository adminGoodsSettlementRepository;
	
	private final AdminGoodsOrdersRepository adminGoodsOrdersRepository;
	
	@Override
	@Transactional
    public Map<String, Object> updateSettlementStatus(List<Map<String, Object>> updatedRows) throws BaCdException {
		long totalOrderPrice = 0;
	    long totalFee = 0;
	    
	    // 1. 먼저 주문 정보를 조회하며 유효성 검사 및 금액 집계
	    List<GoodsOrdersDto> targetOrders = new ArrayList<>();
	    
	    for (Map<String, Object> row : updatedRows) {
	        Long gono = Long.parseLong(row.get("gono").toString());
	        GoodsOrdersDto order = adminGoodsOrdersRepository.findById(gono)
	                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "주문 정보를 찾을 수 없습니다."));

	        // [방어 로직] 이미 정산 완료된 건은 상태 변경을 막음
	        if ("y".equalsIgnoreCase(order.getSettleYn())) {
	            throw new BaCdException(ErrorCode.INVALID_PARAMETER, "이미 정산 완료된 주문(번호: " + gono + ")이 포함되어 있습니다.");
	        }

	        // 금액 집계 (수수료 3.3% 예시)
	        long orderPrice = order.getTotalPrice(); // 주문 테이블의 결제 금액
	        long fee = Math.round(orderPrice * 0.033); // 수수료 계산
	        
	        totalOrderPrice += orderPrice;
	        totalFee += fee;
	        targetOrders.add(order);
	    }

	    if (targetOrders.isEmpty()) {
	        throw new BaCdException(ErrorCode.NOT_FOUND, "정산 처리할 주문이 선택되지 않았습니다.");
	    }

	    // 2. 정산서(GoodsSettlementDto) 생성 및 집계 데이터 삽입
	    GoodsSettlementDto settlement = GoodsSettlementDto.builder()
	            .status("정산완료")
	            .totalAmount(totalOrderPrice)
	            .feeAmount(totalFee)
	            .settleAmount(totalOrderPrice - totalFee)
	            .seller(targetOrders.get(0).getGoods().getMember()) // 첫 번째 주문의 판매자 기준 (판매자별 정산일 경우)
	            .build();
	    
	    GoodsSettlementDto savedSettlement = adminGoodsSettlementRepository.save(settlement);

	    // 3. 주문 정보 업데이트 (FK 연결 및 상태값 변경)
	    for (GoodsOrdersDto order : targetOrders) {
	        order.setSettlement(savedSettlement);
	        order.setSettleYn("y");
	    }

	    Map<String, Object> result = new HashMap<>();
	    result.put("result", true);
	    result.put("settleId", savedSettlement.getSettleId());
	    return result;
    }

	@Override
    public Map<String, Object> list(
    		int page, int perPage, int minPrice, int maxPrice, String settleYn, String delivStatus,
            String category, String status, String search, String sortDir,
            String sortBy, String startDate, String endDate,
			Long minAmount, Long maxAmount) throws BaCdException {

		// SELECT alias 기반 정렬 컬럼
    	String sortColumn = "crdt"; // 기본 컬럼
    	Sort.Direction direction = Sort.Direction.DESC; // 기본 DESC

    	if (sortDir != null && !sortDir.isEmpty()) {
    	    String[] parts = sortDir.split("_");
    	    if (parts.length == 2) {
    	        String col = parts[0];
    	        String dir = parts[1];
    	        // 컬럼 매핑 (DB 컬럼/alias 확인)
    	        switch (col) {
    	            case "crdt": sortColumn = "crdt"; break;           // 주문일
    	            case "price": sortColumn = "total_price"; break;  // 금액
    	            case "rating": sortColumn = "avgRating"; break;   // 평점 (alias)
    	            case "helpful": sortColumn = "reviewCnt"; break;  // 리뷰수 (alias)
    	        }
    	        direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    	    }
    	}

    	Sort sort;
    	if ("avgRating".equals(sortColumn) || "reviewCnt".equals(sortColumn)) {
    	    // alias 정렬 불가 → Java에서 Stream 정렬 처리
    	    sort = Sort.unsorted();
    	} else {
    	    sort = Sort.by(direction, sortColumn);
    	}

    	Pageable pageable = PageRequest.of(page - 1, perPage, sort);
    	
        Page<Map<String, Object>> resultPage = adminGoodsSettlementRepository.findSettlementList(
                search, category, status, delivStatus, settleYn,
                minPrice, maxPrice, startDate, endDate, pageable
        );

        // Grid 형식으로 변환
        return GridUtils.gridRes(resultPage);
    }

}
