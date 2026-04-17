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
import com.project.app.admin.repository.AdminGoodsSettlementRepository;
import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.repository.MemberRepository;
import com.project.app.commissionPolicy.dto.CommissionPolicyDto;
import com.project.app.common.Common;
import com.project.app.common.GridUtils;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.repository.GoodsRepository;
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
    
    /*@Override
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
                order.setDelivStatus((String) row.get("delivStatus"));
            }
            // 정산 처리 로직
            if (row.containsKey("settleYn")) {

                String settleYn = (String) row.get("settleYn");

                // 이미 정산 완료면 막기
                if ("y".equals(order.getSettleYn())) {
                    continue;
                }
                
                // 구매확정 아니면 정산 불가
                /*if (!"구매확정".equals(order.getDelivStatus())) {
                    throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "구매확정 후 정산 가능합니다.");
                }* /
                //세금이나 수수료 테이블
                CommissionPolicyDto policy = commissionPolicyRepository.findById(1L)
                        .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "정책이 없습니다."));

                double pgFeeRate = policy.getPgFeeRate();
                double platformFeeRate = policy.getPlatformFeeRate();
                double taxRate = policy.getTaxRate();

                long orderPrice = order.getTotalPrice();

                long pgFee = Math.round(orderPrice * pgFeeRate);
                long platformFee = Math.round(orderPrice * platformFeeRate);
                long tax = Math.round(orderPrice * taxRate);

                long totalFee = pgFee + platformFee + tax;

                long settleAmount = orderPrice - totalFee;

                // settlement (주문 1건 기준)
                GoodsSettlementDto settlement = GoodsSettlementDto.builder()
                        .status("정산완료")
                        .totalAmount(orderPrice)
                        //.feeAmount(pgFee)
                        .platformFee(platformFee)
                        .taxAmount(tax)
                        .settleAmount(settleAmount)
                        .seller(order.getGoods().getMember())
                        .build();

                settlementRepository.save(settlement);

                // 여기서 환불 체크 (핵심)
                /*boolean hasPendingReturn = order.getReturns() != null &&
                        order.getReturns().stream()
                                .anyMatch(r -> !"완료".equals(r.getReturnStatus()));*/

                /*if (hasPendingReturn) {
                    throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "반품 처리 완료 후 정산 가능합니다.");
                }* /

                order.setSettleYn("y");
                order.setConfirmDate(Timestamp.from(Instant.now()));
            }
        }
		map.put("result", true);
		return map;
    }*/

	@Override
	@Transactional
	public Map<String, Object> updateOrders(List<Map<String, Object>> updatedRows) throws BaCdException {
	
	    Map<String, Object> map = new HashMap<>();
	    if (updatedRows == null || updatedRows.isEmpty()) return map;
	
	    // 1. 주문 조회
	    List<GoodsOrdersDto> orders = new ArrayList<>();
	
	    for (Map<String, Object> row : updatedRows) {
	
	        Object gonoObj = row.get("gono");
	        if (gonoObj == null) continue;
	
	        Long gono = (gonoObj instanceof Number)
	                ? ((Number) gonoObj).longValue()
	                : Long.parseLong(String.valueOf(gonoObj));
	
	        GoodsOrdersDto order = orderRepository.findById(gono)
	                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));
	
	        // 배송만 변경
	        if (row.containsKey("delivStatus")) {
	            order.setDelivStatus((String) row.get("delivStatus"));
	        }
	
	        orders.add(order);
	    }
	
	    if (orders.isEmpty()) {
	        map.put("result", false);
	        return map;
	    }
	
	    // 2. 이미 정산된 주문 체크 (전체 선검증)
	    for (GoodsOrdersDto o : orders) {
	        if ("y".equals(o.getSettleYn())) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "이미 정산된 주문 포함");
	        }
	    }
	
	    // 3. 판매자 기준 그룹핑 (핵심)
	    Map<MemberDto, List<GoodsOrdersDto>> grouped =
	            orders.stream()
	                    .collect(Collectors.groupingBy(o -> o.getGoods().getMember()));
	
	    // 4. 정책 1번만 조회
	    CommissionPolicyDto policy = commissionPolicyRepository.findById(1L)
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "정책이 없습니다."));
	
	    double pgFeeRate = policy.getPgFeeRate();
	    double platformFeeRate = policy.getPlatformFeeRate();
	    double taxRate = policy.getTaxRate();
	
	    // 5. 판매자별 정산 생성
	    for (MemberDto seller : grouped.keySet()) {
	
	        List<GoodsOrdersDto> sellerOrders = grouped.get(seller);
	
	        long totalAmount = 0;
	        long pgFee = 0;
	        long platformFee = 0;
	        long tax = 0;
	
	        // 6. 주문 합산
	        for (GoodsOrdersDto o : sellerOrders) {
	
	            long price = o.getTotalPrice();
	
	            totalAmount += price;
	            pgFee += Math.round(price * pgFeeRate);
	            platformFee += Math.round(price * platformFeeRate);
	            tax += Math.round(price * taxRate);
	        }
	
	        long totalFee = pgFee + platformFee + tax;
	        long settleAmount = totalAmount - totalFee;
	
	        // 7. 정산 1개 생성 (판매자 기준)
	        GoodsSettlementDto settlement = GoodsSettlementDto.builder()
	                .status("정산완료")
	                .totalAmount(totalAmount)
	                .pgFee(pgFee)
	                .platformFee(platformFee)
	                .taxAmount(tax)
	                .settleAmount(settleAmount)
	                .seller(seller)
	                .build();
	
	        settlementRepository.save(settlement);
	
	        // 8. 주문에 FK 연결
	        for (GoodsOrdersDto o : sellerOrders) {
	            o.setSettleYn("y");
	            o.setSettlement(settlement);
	            o.setConfirmDate(Timestamp.from(Instant.now()));
	        }
	    }
	
	    map.put("result", true);
	    return map;
	}
    
}
