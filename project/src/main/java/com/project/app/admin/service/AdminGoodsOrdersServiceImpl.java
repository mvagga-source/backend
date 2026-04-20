package com.project.app.admin.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import com.project.app.admin.repository.AdminCommissionPolicyRepository;
import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminGoodsReturnRepository;
import com.project.app.admin.repository.AdminGoodsSettlementRepository;
import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.repository.MemberRepository;
import com.project.app.commissionPolicy.dto.CommissionPolicyDto;
import com.project.app.common.Common;
import com.project.app.common.GridUtils;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.repository.GoodsRepository;
import com.project.app.goodsReturn.dto.GoodsReturnDto;
import com.project.app.goodsSettlement.dto.GoodsSettlementDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminGoodsOrdersServiceImpl implements AdminGoodsOrdersService {
	private final AdminGoodsOrdersRepository orderRepository;
	
	private final AdminGoodsSettlementRepository settlementRepository;
	
	private final AdminCommissionPolicyRepository commissionPolicyRepository;
	
	private final AdminGoodsReturnRepository returnRepository;
    //private final MemberRepository memberRepository; // 멤버 정보 확인용
    //private final GoodsRepository goodsRepository;   // 상품 정보 확인용

    @Override
    public Map<String, Object> list(int page, int perPage, int minPrice, int maxPrice, String settleYn, String delivStatus,
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
    	
        Page<Map<String, Object>> resultPage = orderRepository.findAdminOrdersMap(
                search, category, status, delivStatus, settleYn,
                minPrice, maxPrice, startDate, endDate, pageable
        );

        // Grid 형식으로 변환
        return GridUtils.gridRes(resultPage);
    }
    
    @Override
    @Transactional
    public Map<String, Object> updateOrders(List<Map<String, Object>> updatedRows) throws BaCdException {
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

            GoodsOrdersDto order = orderRepository.findById(gono)
                    .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND)); 

            // 필드 업데이트
            if (row.containsKey("delivStatus")) {
            	String newStatus = (String) row.get("delivStatus");
                String oldStatus = order.getDelivStatus();
                // 이미 정산 완료면 무조건 차단
                if ("y".equals(order.getSettleYn())) {
                    throw new BaCdException(
                        ErrorCode.INVALID_INPUT_VALUE,
                        "이미 정산 완료된 주문은 배송상태를 변경할 수 없습니다."
                    );
                }
                else if ("배송완료".equals(newStatus)) {
                	order.setDeliveryCompleteDate(new Timestamp(System.currentTimeMillis()));
                }
            	// '구매확정'으로 변경하려는 경우 체크
                else if ("구매확정".equals(newStatus) && !"구매확정".equals(oldStatus)) {
                    
                    // 이미 정산이 완료된 건인지 확인 (보안상 추가)
                    if ("y".equals(order.getSettleYn())) {
                        throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "이미 정산 완료된 주문은 상태를 변경할 수 없습니다.");
                    }

                    // 반품/교환 진행 중인지 확인
                    // 주문에 연결된 반품 정보가 있고, 그 상태가 '완료'나 '거부', '취소'가 아닌 경우 (즉, 진행 중)
                    // 만약 GoodsOrdersDto에 List<GoodsReturnDto> returns가 정의되어 있다면:
                    List<GoodsReturnDto> goodsReturn = returnRepository.findAllByOrderAndDelYn(order,"n");
                    boolean isReturnProcessing = goodsReturn != null && goodsReturn.stream()
                            .anyMatch(r -> !"완료".equals(r.getReturnStatus()) 
                                        && !"거부".equals(r.getReturnStatus()) 
                                        && !"취소".equals(r.getReturnStatus()));
                    
                    if (isReturnProcessing) {
                        throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, 
                            "주문번호[" + order.getOrderId() + "]는 반품/교환 절차가 진행 중이므로 구매확정할 수 없습니다.");
                    }

                    // 구매확정 일자 세팅
                    order.setConfirmDate(new Timestamp(System.currentTimeMillis()));
                }
                order.setDelivStatus(newStatus);
            }
        }
		map.put("result", true);
		return map;
    }
    
}
