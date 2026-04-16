package com.project.app.admin.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminGoodsReturnRepository;
import com.project.app.auth.repository.MemberRepository;
import com.project.app.common.Common;
import com.project.app.common.GridUtils;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.repository.GoodsRepository;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminGoodsReturnServiceImpl implements AdminGoodsReturnService {
	private final AdminGoodsReturnRepository returnRepository;
    //private final MemberRepository memberRepository; // 멤버 정보 확인용
    //private final GoodsRepository goodsRepository;   // 상품 정보 확인용

    @Override
    public Map<String, Object> list(int page, int perPage, int minPrice, int maxPrice, String settleYn, String delivStatus, String returnStatus,
                                    String category, String status, String search, String sortDir,
                                    String sortBy, String startDate, String endDate) throws BaCdException {
    	LocalDateTime startDt = Common.parseDate(startDate, true);
    	LocalDateTime endDt = Common.parseDate(endDate, false);

    	// 시작일 > 종료일 체크
    	if (startDt != null && endDt != null && startDt.isAfter(endDt)) {
    	    throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "시작일은 종료일보다 클 수 없습니다.");
    	}

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
    	
        Page<Map<String, Object>> resultPage = returnRepository.findAdminOrdersMap(
                search, category, status, delivStatus, returnStatus, settleYn,
                minPrice, maxPrice, startDate, endDate, pageable
        );

        // Grid 형식으로 변환
        return GridUtils.gridRes(resultPage);
    }
    
    @Override
    @Transactional
    public Map<String, Object> update(List<Map<String, Object>> updatedRows) throws BaCdException {
    	Map<String, Object> map = new HashMap<>();
    	if (updatedRows == null || updatedRows.isEmpty()) return map;

        for (Map<String, Object> row : updatedRows) {
            Object gonoObj = row.get("gono");
            if (gonoObj == null) continue;

            Long gono;
            if (gonoObj instanceof Number) {
                gono = ((Number) gonoObj).longValue();
            } else {
                gono = Long.parseLong(String.valueOf(gonoObj));
            }

            GoodsOrdersDto order = returnRepository.findById(gono)
                    .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND)); 

            // 필드 업데이트
            if (row.containsKey("delivStatus")) {
                order.setDelivStatus((String) row.get("delivStatus"));
            }
            if (row.containsKey("settleYn")) {
                order.setSettleYn((String) row.get("settleYn"));
            }
        }
		map.put("result", true);
		return map;
    }
    
}
