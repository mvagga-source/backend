package com.project.app.admin.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminCommissionPolicyRepository;
import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminGoodsRepository;
import com.project.app.admin.repository.AdminGoodsReturnRepository;
import com.project.app.admin.repository.AdminGoodsReviewRepository;
import com.project.app.admin.repository.AdminGoodsSettlementRepository;
import com.project.app.auth.dto.MemberDto;
import com.project.app.commissionPolicy.dto.CommissionPolicyDto;
import com.project.app.common.Common;
import com.project.app.common.GridUtils;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsReturn.dto.GoodsReturnDto;
import com.project.app.goodsSettlement.dto.GoodsSettlementDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.notification.dto.NotificationDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminGoodsSettlementServiceImpl implements AdminGoodsSettlementService {
	private final AdminGoodsSettlementRepository adminGoodsSettlementRepository;
	
	private final AdminGoodsOrdersRepository adminGoodsOrdersRepository;
	
	private final AdminCommissionPolicyRepository commissionPolicyRepository;
	
	private final AdminGoodsReturnRepository adminGoodsReturnRepository;
	
	private final ApplicationEventPublisher eventPublisher;
	
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
	            //.feeAmount(totalFee)
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
    	
        Page<Map<String, Object>> resultPage = adminGoodsSettlementRepository.findSettlementList(
                search, category, status, settleYn,
                minAmount, maxAmount, startDate, endDate, pageable
        );

        // Grid 형식으로 변환
        return GridUtils.gridRes(resultPage);
    }
	
	/**
	 * 주문 ID 목록(orderIds)을 받아 판매자별로 정산을 생성하고 주문에 연결
	 * 교환은 따로 테이블을 추가해서 진행해야 함(시간상 진행은 안 함)
	 * 컬럼도 주문번호나 정보등 좀 다름
	 */
	/*public Map<String, Object> settleOrders(List<Long> orderIds, MemberDto admin) throws BaCdException {
	    if (orderIds == null || orderIds.isEmpty()) {
	        throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "정산할 주문 ID가 제공되지 않았습니다.");
	    }

	    // 1) 주문 조회
	    List<GoodsOrdersDto> orders = adminGoodsOrdersRepository.findAllById(orderIds);
	    if (orders.isEmpty()) {
	        throw new BaCdException(ErrorCode.NOT_FOUND, "주문 내역이 존재하지 않습니다.");
	    }

	    // 2) 정산 정책 조회
	    CommissionPolicyDto policy = commissionPolicyRepository.findById(1L)
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "정산 정책을 찾을 수 없습니다."));

	    BigDecimal pgRate = BigDecimal.valueOf(policy.getPgFeeRate());
	    BigDecimal platformRate = BigDecimal.valueOf(policy.getPlatformFeeRate());
	    BigDecimal taxRate = BigDecimal.valueOf(policy.getTaxRate());
	    
	    // 3) 사전 검증: 각 주문이 정산 가능한 상태인지 확인
	    for (GoodsOrdersDto o : orders) {
	        if (!"PAID".equals(o.getStatus())) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "결제 완료만 정산 가능합니다. 주문번호:" + o.getOrderId());
	        }
	        if (!"구매확정".equals(o.getDelivStatus())) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "구매확정 후 정산 가능합니다. 주문번호:" + o.getOrderId());
	        }
	        if ("y".equals(o.getSettleYn())) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "이미 정산된 주문입니다. 주문번호:" + o.getOrderId());
	        }

	        // 반품 진행중인 주문은 정산 불가 (진행중인 반품이 있으면 예외)
	        List<GoodsReturnDto> returns = adminGoodsReturnRepository.findAllByOrderAndDelYn(o, "n");
	        boolean hasPendingReturn = returns.stream()
	                .anyMatch(r -> !"완료".equals(r.getReturnStatus())
	                        && !"취소".equals(r.getReturnStatus())
	                        && !"거부".equals(r.getReturnStatus()));
	        if (hasPendingReturn) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "반품 진행중인 주문은 정산 불가합니다. 주문번호: " + o.getOrderId());
	        }
	    }

	    // 4) 판매자별 그룹핑 (같은 판매자끼리 묶어서 정산 생성)
	    Map<MemberDto, List<GoodsOrdersDto>> grouped =
	            orders.stream().collect(Collectors.groupingBy(o -> o.getGoods().getMember()));

	    // 결과 반환용
	    Map<String, Object> result = new HashMap<>();
	    List<Long> createdSettlementIds = new ArrayList<>();

	    // 5) 판매자별 정산 처리
	    for (Map.Entry<MemberDto, List<GoodsOrdersDto>> entry : grouped.entrySet()) {
	        MemberDto seller = entry.getKey();
	        List<GoodsOrdersDto> sellerOrders = entry.getValue();

	        // 누적 변수 (BigDecimal 사용)
	        BigDecimal totalProductSales = BigDecimal.ZERO; // 실제 판매된 순수 상품 매출 합계 (환불 반영 후)
	        BigDecimal totalPenalty = BigDecimal.ZERO;      // 판매자 귀책으로 인한 배송비 패널티 합계
	        BigDecimal totalRefund = BigDecimal.ZERO;       // 구매자에게 환불된 총액 합계
	        BigDecimal totalAmount = BigDecimal.ZERO;   // 정산 대상 총액 (환불/배송패널티 반영 후)
	        BigDecimal refundAmount = BigDecimal.ZERO;  // 환불 합계(판매자에게 차감되는 금액)

	        // 각 주문별로 환불/배송패널티 누적 계산
	        for (GoodsOrdersDto o : sellerOrders) {
	        	
	        	// o.getTotalPrice()는 '상품가 + 배송비'이므로, 여기서 배송비를 빼서 순수 상품가만 추출함
	            BigDecimal fullOrderPrice = BigDecimal.valueOf(o.getTotalPrice() != null ? o.getTotalPrice() : 0L);
	            BigDecimal initialShippingFee = BigDecimal.valueOf(o.getGdelPrice() != null ? o.getGdelPrice() : 0L);
	            BigDecimal pureProductPrice = fullOrderPrice.subtract(initialShippingFee); // 실제 정산 대상(상품값)
	            
	            BigDecimal orderRefundSum = BigDecimal.ZERO;     // 이 주문에서 발생한 총 환불액
	            BigDecimal orderPenaltySum = BigDecimal.ZERO;    // 이 주문에서 발생한 총 패널티(배송비)

	            // 반품 목록 조회 (삭제되지 않은 것만)
	            List<GoodsReturnDto> returns = adminGoodsReturnRepository.findAllByOrderAndDelYn(o, "n");

	            // 반품 요청별로 누적 처리 (shippingApplied 제거 — 모든 요청을 누적)
	            for (GoodsReturnDto r : returns) {
	            	if (!"완료".equals(r.getReturnStatus())) continue;
	            	
	            	// 타입에 따른 환불액 처리
	                // '반품'일 때만 환불액을 누적, '교환'은 매출을 깎지 말아야 함
	                if ("반품".equals(r.getReturnType())) {
	                    BigDecimal rRefund = BigDecimal.valueOf(r.getRefundPrice() != null ? r.getRefundPrice() : 0L);
	                    orderRefundSum = orderRefundSum.add(rRefund);
	                }

	                // 배송비 패널티 계산 정책 (요청 단위 적용)
	                // - 변심: 구매자 부담 -> 패널티 없음
	                // - 판매자 귀책(오배송/파손 등): 판매자 부담 -> 회수비 또는 왕복비
	                // - 교환: 상품 환불 없음, 배송비는 왕복 또는 회수비
	                if (!"변심".equals(r.getReturnReason())) {
	                    BigDecimal unitPenalty = BigDecimal.valueOf(r.getGdelPrice() != null ? r.getGdelPrice() : 3000L);
	                    
	                    // 반품: 수거비
	                    orderPenaltySum = orderPenaltySum.add(unitPenalty);
	                }
	            }

	            // 주문별 최종 매출 기여도 확정(주문의 최종 기여 금액 = 주문총액 - 주문환불합계 - 주문배송패널티)
	            // 최종 상품 매출 = 순수 상품가 - 환불액 (전량 반품 시 0원이 됨)
	            BigDecimal finalOrderSales = pureProductPrice.subtract(orderRefundSum);
	            if (finalOrderSales.compareTo(BigDecimal.ZERO) < 0) finalOrderSales = BigDecimal.ZERO;

	            // 판매자 전체 합계에 누적
	            totalProductSales = totalProductSales.add(finalOrderSales); // 수수료/세금의 기준점이 됨
	            totalPenalty = totalPenalty.add(orderPenaltySum);          // 나중에 최종 금액에서 뺌
	            totalRefund = totalRefund.add(orderRefundSum);             // 단순 기록용
	        }

	        // 6) 수수료/세금 계산 (정책 비율 적용, 반올림은 원 단위로 처리)
	        BigDecimal pgFee = BigDecimal.ZERO;
	        BigDecimal platformFee = BigDecimal.ZERO;
	        BigDecimal tax = BigDecimal.ZERO;

	        if (totalProductSales.compareTo(BigDecimal.ZERO) > 0) {
	            pgFee = totalProductSales.multiply(pgRate).setScale(0, RoundingMode.HALF_UP);
	            platformFee = totalProductSales.multiply(platformRate).setScale(0, RoundingMode.HALF_UP);
	            
	            // 세금은 (매출 - PG수수료 - 플랫폼수수료)의 10%로 계산
	            BigDecimal profitAfterFee = totalProductSales.subtract(pgFee).subtract(platformFee);
	            if (profitAfterFee.compareTo(BigDecimal.ZERO) > 0) {
	                tax = profitAfterFee.multiply(taxRate).setScale(0, RoundingMode.HALF_UP);
	            }
	        }

	        // 7) 최종 정산 금액 확정
	        // 최종금액 = 매출 - 모든수수료 - 세금 - 패널티(배송비)
	        // 이 금액은 마이너스가 될 수 있음 (판매자가 물어내야 할 배송비가 더 큰 경우)
	        BigDecimal finalSettleAmount = totalProductSales
	                .subtract(pgFee)
	                .subtract(platformFee)
	                .subtract(tax)
	                .subtract(totalPenalty);

	        // 7) 정산 레코드 생성: 음수 허용(요구사항에 따라 지급 후 판매자에게 고지)
	        GoodsSettlementDto settlement = GoodsSettlementDto.builder()
	                .seller(seller)
	                .totalAmount(totalProductSales.longValue())
	                .refundAmount(totalRefund.longValue())
	                .pgFee(pgFee.longValue())
	                .platformFee(platformFee.longValue())
	                .taxAmount(tax.longValue())
	                .settleAmount(finalSettleAmount.longValue()) // 음수 가능
	                .status("정산완료") // 음수여도 '정산완료'로 기록; 필요시 별도 고지 플래그 추가
	                .build();

	        GoodsSettlementDto savedSettlement = adminGoodsSettlementRepository.save(settlement);
	        createdSettlementIds.add(savedSettlement.getSettleId());

	        // 8) 주문에 정산 연결 및 상태 업데이트 (정산 완료로 표시)
	        for (GoodsOrdersDto o : sellerOrders) {
	            o.setSettlement(savedSettlement);
	            o.setSettleYn("y");
	        }
	        // 명시적 저장: JPA 변경 감지에 의존하지 않고 확실히 DB 반영
	        adminGoodsOrdersRepository.saveAll(sellerOrders);
	        
	        //굿즈판매자에게 정산 알림 전송
	        NotificationDto sellerEvent = NotificationDto.builder()
	                .member(seller)		//판매자아이디
	                .sender(admin)		//관리자아이디
	                .nocontent(seller.getNickname()+"님, 이번달 정산이 완료되었습니다.")
	                .type("GOODS_TRADE") // 설정에서 allowQnaAnswer로 체크됨
	                .url("/MyMain/MySaleReturn")
	                .isRead("n")
	                .build();
	        eventPublisher.publishEvent(sellerEvent);
	        
	        //교환일 경우 다시 교환테이블에 넣는 과정이 필요(배송상태 등)
	    }
	    result.put("result", true);
	    result.put("settleIds", createdSettlementIds);
	    return result;
	}*/
	
	/**
	 * 주문 ID 목록(orderIds)을 받아 판매자별로 정산을 생성하고 주문에 연결
	 * 교환은 보통 주문 다시 생성하는 방식으로 진행(주문테이블에 넣어도 되지만 흐름이 이상해져서 상태 등 생각할게 많음)
	 * 교환까지 생각하고 배치로 원래 정산일시에 맞춰서 진행도 해야되지만 배치없이 짧게 줄여서 진행
	 * 교환은 주문정보 등 컬럼도 다르고 테이블 나눠서 관리하기 편함
	 */
	public Map<String, Object> settleOrders(List<Long> orderIds, MemberDto admin) throws BaCdException {
	    if (orderIds == null || orderIds.isEmpty()) {
	        throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "정산할 주문 ID가 제공되지 않았습니다.");
	    }

	    // 1) 주문 조회
	    List<GoodsOrdersDto> orders = adminGoodsOrdersRepository.findAllById(orderIds);
	    if (orders.isEmpty()) {
	        throw new BaCdException(ErrorCode.NOT_FOUND, "주문 내역이 존재하지 않습니다.");
	    }

	    // 2) 정산 정책 조회
	    CommissionPolicyDto policy = commissionPolicyRepository.findById(1L)
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "정산 정책을 찾을 수 없습니다."));

	    BigDecimal pgRate = BigDecimal.valueOf(policy.getPgFeeRate());
	    BigDecimal platformRate = BigDecimal.valueOf(policy.getPlatformFeeRate());
	    BigDecimal taxRate = BigDecimal.valueOf(policy.getTaxRate());
	    
	    // 3) 사전 검증: 각 주문이 정산 가능한 상태인지 확인
	    for (GoodsOrdersDto o : orders) {
	        if (!"PAID".equals(o.getStatus())) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "결제 완료만 정산 가능합니다. 주문번호:" + o.getOrderId());
	        }
	        if (!"구매확정".equals(o.getDelivStatus())) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "구매확정 후 정산 가능합니다. 주문번호:" + o.getOrderId());
	        }
	        if ("y".equals(o.getSettleYn())) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "이미 정산된 주문입니다. 주문번호:" + o.getOrderId());
	        }

	        // 반품 진행중인 주문은 정산 불가 (진행중인 반품이 있으면 예외)
	        List<GoodsReturnDto> returns = adminGoodsReturnRepository.findAllByOrderAndDelYn(o, "n");
	        boolean hasPendingReturn = returns.stream()
	                .anyMatch(r -> !"완료".equals(r.getReturnStatus())
	                        && !"취소".equals(r.getReturnStatus())
	                        && !"거부".equals(r.getReturnStatus()));
	        if (hasPendingReturn) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "반품 진행중인 주문은 정산 불가합니다. 주문번호: " + o.getOrderId());
	        }
	    }

	    // 4) 판매자별 그룹핑 (같은 판매자끼리 묶어서 정산 생성)
	    Map<MemberDto, List<GoodsOrdersDto>> grouped =
	            orders.stream().collect(Collectors.groupingBy(o -> o.getGoods().getMember()));

	    // 결과 반환용
	    Map<String, Object> result = new HashMap<>();
	    List<Long> createdSettlementIds = new ArrayList<>();

	    // 5) 판매자별 정산 처리
	    for (Map.Entry<MemberDto, List<GoodsOrdersDto>> entry : grouped.entrySet()) {
	        MemberDto seller = entry.getKey();
	        List<GoodsOrdersDto> sellerOrders = entry.getValue();

	        // 누적 변수 (BigDecimal 사용)
	        BigDecimal totalProductSales = BigDecimal.ZERO; // 실제 판매된 순수 상품 매출 합계 (환불 반영 후)
	        BigDecimal totalPenalty = BigDecimal.ZERO;      // 판매자 귀책으로 인한 배송비 패널티 합계
	        BigDecimal totalRefund = BigDecimal.ZERO;       // 구매자에게 환불된 총액 합계
	        BigDecimal totalAmount = BigDecimal.ZERO;   // 정산 대상 총액 (환불/배송패널티 반영 후)
	        BigDecimal refundAmount = BigDecimal.ZERO;  // 환불 합계(판매자에게 차감되는 금액)

	        // 각 주문별로 환불/배송패널티 누적 계산
	        for (GoodsOrdersDto o : sellerOrders) {
	        	
	        	// o.getTotalPrice()는 '상품가 + 배송비'이므로, 여기서 배송비를 빼서 순수 상품가만 추출함
	            BigDecimal fullOrderPrice = BigDecimal.valueOf(o.getTotalPrice() != null ? o.getTotalPrice() : 0L);
	            BigDecimal initialShippingFee = BigDecimal.valueOf(o.getGdelPrice() != null ? o.getGdelPrice() : 0L);
	            BigDecimal pureProductPrice = fullOrderPrice.subtract(initialShippingFee); // 실제 정산 대상(상품값)
	            
	            BigDecimal orderRefundSum = BigDecimal.ZERO;     // 이 주문에서 발생한 총 환불액
	            BigDecimal orderPenaltySum = BigDecimal.ZERO;    // 이 주문에서 발생한 총 패널티(배송비)

	            // 반품 목록 조회 (삭제되지 않은 것만)
	            List<GoodsReturnDto> returns = adminGoodsReturnRepository.findAllByOrderAndDelYn(o, "n");

	            // 반품 요청별로 누적 처리 (shippingApplied 제거 — 모든 요청을 누적)
	            for (GoodsReturnDto r : returns) {
	            	if (!"완료".equals(r.getReturnStatus())) continue;
	            	
	            	// 타입에 따른 환불액 처리
	                // '반품'일 때만 환불액을 누적, '교환'은 매출을 깎지 말아야 함
	                if ("반품".equals(r.getReturnType())) {
	                    BigDecimal rRefund = BigDecimal.valueOf(r.getRefundPrice() != null ? r.getRefundPrice() : 0L);
	                    orderRefundSum = orderRefundSum.add(rRefund);
	                }

	                // 배송비 패널티 계산 정책 (요청 단위 적용)
	                // - 변심: 구매자 부담 -> 패널티 없음
	                // - 판매자 귀책(오배송/파손 등): 판매자 부담 -> 회수비 또는 왕복비
	                // - 교환: 상품 환불 없음, 배송비는 왕복 또는 회수비
	                if (!"변심".equals(r.getReturnReason())) {
	                    BigDecimal unitPenalty = BigDecimal.valueOf(r.getGdelPrice() != null ? r.getGdelPrice() : 3000L);
	                    
	                    if ("교환".equals(r.getReturnType())) {
	                        // 교환: 수거비 + 재배송비 = 왕복 (6,000원)
	                        orderPenaltySum = orderPenaltySum.add(unitPenalty.multiply(BigDecimal.valueOf(2)));
	                    } else {
	                        // 반품: 수거비 (3,000원)
	                        orderPenaltySum = orderPenaltySum.add(unitPenalty);
	                    }
	                }
	            }

	            // 주문별 최종 매출 기여도 확정(주문의 최종 기여 금액 = 주문총액 - 주문환불합계 - 주문배송패널티)
	            // 최종 상품 매출 = 순수 상품가 - 환불액 (전량 반품 시 0원이 됨)
	            BigDecimal finalOrderSales = pureProductPrice.subtract(orderRefundSum);
	            if (finalOrderSales.compareTo(BigDecimal.ZERO) < 0) finalOrderSales = BigDecimal.ZERO;

	            // 판매자 전체 합계에 누적
	            totalProductSales = totalProductSales.add(finalOrderSales); // 수수료/세금의 기준점이 됨
	            totalPenalty = totalPenalty.add(orderPenaltySum);          // 나중에 최종 금액에서 뺌
	            totalRefund = totalRefund.add(orderRefundSum);             // 단순 기록용
	        }

	        // 6) 수수료/세금 계산 (정책 비율 적용, 반올림은 원 단위로 처리)
	        BigDecimal pgFee = BigDecimal.ZERO;
	        BigDecimal platformFee = BigDecimal.ZERO;
	        BigDecimal tax = BigDecimal.ZERO;

	        if (totalProductSales.compareTo(BigDecimal.ZERO) > 0) {
	            pgFee = totalProductSales.multiply(pgRate).setScale(0, RoundingMode.HALF_UP);
	            platformFee = totalProductSales.multiply(platformRate).setScale(0, RoundingMode.HALF_UP);
	            
	            // 세금은 (매출 - PG수수료 - 플랫폼수수료)의 10%로 계산
	            BigDecimal profitAfterFee = totalProductSales.subtract(pgFee).subtract(platformFee);
	            if (profitAfterFee.compareTo(BigDecimal.ZERO) > 0) {
	                tax = profitAfterFee.multiply(taxRate).setScale(0, RoundingMode.HALF_UP);
	            }
	        }

	        // 7) 최종 정산 금액 확정
	        // 최종금액 = 매출 - 모든수수료 - 세금 - 패널티(배송비)
	        // 이 금액은 마이너스가 될 수 있음 (판매자가 물어내야 할 배송비가 더 큰 경우)
	        BigDecimal finalSettleAmount = totalProductSales
	                .subtract(pgFee)
	                .subtract(platformFee)
	                .subtract(tax)
	                .subtract(totalPenalty);

	        // 7) 정산 레코드 생성: 음수 허용(요구사항에 따라 지급 후 판매자에게 고지)
	        GoodsSettlementDto settlement = GoodsSettlementDto.builder()
	                .seller(seller)
	                .totalAmount(totalProductSales.longValue())
	                .refundAmount(totalRefund.longValue())
	                .pgFee(pgFee.longValue())
	                .platformFee(platformFee.longValue())
	                .taxAmount(tax.longValue())
	                .settleAmount(finalSettleAmount.longValue()) // 음수 가능
	                .status("정산완료") // 음수여도 '정산완료'로 기록; 필요시 별도 고지 플래그 추가
	                .build();

	        GoodsSettlementDto savedSettlement = adminGoodsSettlementRepository.save(settlement);
	        createdSettlementIds.add(savedSettlement.getSettleId());

	        // 8) 주문에 정산 연결 및 상태 업데이트 (정산 완료로 표시)
	        for (GoodsOrdersDto o : sellerOrders) {
	            o.setSettlement(savedSettlement);
	            o.setSettleYn("y");
	        }
	        // 명시적 저장: JPA 변경 감지에 의존하지 않고 확실히 DB 반영
	        adminGoodsOrdersRepository.saveAll(sellerOrders);

	        // 9) 음수인 경우 추가 조치: 판매자 고지(알림/이메일/관리자 로그)
	        /*if (finalSettleAmount.compareTo(BigDecimal.ZERO) < 0) {}*/
	        
	        //굿즈판매자에게 정산 알림 전송
	        NotificationDto sellerEvent = NotificationDto.builder()
	                .member(seller)		//판매자아이디
	                .sender(admin)		//관리자아이디
	                .nocontent(seller.getNickname()+"님, 이번달 정산이 완료되었습니다.")
	                .type("GOODS_TRADE") // 설정에서 allowQnaAnswer로 체크됨
	                .url("/MyMain/MySaleReturn")
	                .isRead("n")
	                .build();
	        eventPublisher.publishEvent(sellerEvent);
	    }
	    result.put("result", true);
	    result.put("settleIds", createdSettlementIds);
	    return result;
	}

}
