package com.project.app.admin.service;

import java.sql.Timestamp;
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
import com.project.app.goodsReturn.dto.GoodsReturnDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminGoodsReturnServiceImpl implements AdminGoodsReturnService {
	private final AdminGoodsReturnRepository returnRepository;
	private final AdminGoodsOrdersRepository ordersRepository;
    //private final MemberRepository memberRepository; // 멤버 정보 확인용
    //private final GoodsRepository goodsRepository;   // 상품 정보 확인용

    @Override
    public Map<String, Object> list(int page, int perPage, int minPrice, int maxPrice, String settleYn, String delivStatus, 
    								String returnStatus, String returnType, String returnReason,
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
                search, category, status, delivStatus, returnStatus, returnType, returnReason, settleYn,
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
            Object rnoObj = row.get("rno");
            if (gonoObj == null) continue;

            Long gono;
            Long rno;
            if (gonoObj instanceof Number) {
                gono = ((Number) gonoObj).longValue();
            } else {
                gono = Long.parseLong(String.valueOf(gonoObj));
            }
            if (rnoObj instanceof Number) {
                rno = ((Number) rnoObj).longValue();
            } else {
                rno = Long.parseLong(String.valueOf(rnoObj));
            }
            
            if (gono == null) continue;
            if (rno == null) continue;

            // 2. 주문 정보 조회
            GoodsOrdersDto order = ordersRepository.findById(gono)
                    .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "주문 정보를 찾을 수 없습니다. (ID: " + gono + ")"));

            // 구매확정된 주문은 반품 수정 불가
            if ("구매확정".equals(order.getDelivStatus())) {
                throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "이미 구매확정된 주문은 반품 정보를 수정할 수 없습니다.");
            } else if (!"배송완료".equals(order.getDelivStatus())) {
            	throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "배송완료되지 않은 상품은 반품하실 수 없습니다.");
            } else if ("y".equals(order.getSettleYn())) {
                throw new BaCdException(ErrorCode.COMPLETE_STATUS, "이미 정산된 주문은 반품 수정 불가합니다.");
            }

            // 반품 정보
            GoodsReturnDto returnDto = returnRepository.findByRnoAndDelYn(rno, "n").orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "반품 정보를 찾을 수 없습니다. (ID: " + rno + ")"));
            
            // 반품 수량 검증 로직
            if (row.containsKey("returnCnt")) {
            	Long newReturnCnt = Long.parseLong(String.valueOf(row.get("returnCnt")));
            	if (newReturnCnt <= 0) {
            	    throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품 수량은 1개 이상이어야 합니다.");
            	}
            	Long otherReturnCnt = returnRepository.sumReturnCntByGonoExceptRno(gono, rno); // null 방지면 COALESCE 이미 있음

        		Long orderCnt = order.getCnt();

        		Long totalAfterUpdate = otherReturnCnt + newReturnCnt;

        		if (totalAfterUpdate > orderCnt) {
        		    throw new BaCdException(
        		        ErrorCode.INVALID_INPUT_VALUE,
        		        String.format("반품 총 수량(%d)은 주문 수량(%d)을 초과할 수 없습니다. (기존 반품:%d, 입력:%d)", 
        		        		totalAfterUpdate, orderCnt, otherReturnCnt, newReturnCnt)
        		    );
        		}

                // 전체 반품 시 상태 처리(리뷰 못 사용하게 등, 이미 리뷰를 쓴 경우 구매확정으로 관리자도 수정안되게 막음)
                /*if (totalAfterUpdate == orderCnt) {
                	
                }*/
        		returnDto.setReturnCnt(newReturnCnt);
            }
            if (row.containsKey("delivStatus")) {
            	/*if("배송완료".equals(row.get("delivStatus"))) {
            		returnDto.setReturnStatus((String) row.get("검수대기"));		//변하지 않음 관리자가 직접 수정 필요
            	}*/
            	returnDto.setDelivStatus((String) row.get("delivStatus"));
            }
            if (row.containsKey("returnStatus")) {
            	if("완료".equals(row.get("returnStatus"))) {
            		returnDto.setCompleteDate(new Timestamp(System.currentTimeMillis()));
            	}
            	returnDto.setReturnStatus((String) row.get("returnStatus"));
            }
            returnRepository.save(returnDto);
        }
		map.put("result", true);
		return map;
    }
    
}
