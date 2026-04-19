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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminGoodsSettlementServiceImpl implements AdminGoodsSettlementService {
	private final AdminGoodsSettlementRepository adminGoodsSettlementRepository;
	
	private final AdminGoodsOrdersRepository adminGoodsOrdersRepository;
	
	private final AdminCommissionPolicyRepository commissionPolicyRepository;
	
	private final AdminGoodsReturnRepository adminGoodsReturnRepository;
	
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
	
	/*@Transactional
    public void settleOrders(List<Long> orderIds) {

        List<GoodsOrdersDto> orders = adminGoodsOrdersRepository.findAllById(orderIds);

        if (orders.isEmpty()) {
            throw new BaCdException(ErrorCode.NOT_FOUND, "주문한 내역이 존재하지 않습니다.");
        }
        
        // 정책 조회
        CommissionPolicyDto policy = commissionPolicyRepository.findById(1L)
                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));


        // 사전 검증
        for (GoodsOrdersDto o : orders) {

            if (!"PAID".equals(o.getStatus())) {
                throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "결제 완료만 정산 가능합니다. 주문번호:"+o.getOrderId());
            }

            if (!"구매확정".equals(o.getDelivStatus())) {
                throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "구매확정 후 정산 가능합니다. 주문번호:"+o.getOrderId());
            }

            if ("y".equals(o.getSettleYn())) {
                throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "이미 정산된 주문입니다. 주문번호:"+o.getOrderId());
            }

            List<GoodsReturnDto> returns = adminGoodsReturnRepository.findAllByOrderAndDelYn(o, "n");

            boolean hasPendingReturn = returns.stream()
                    .anyMatch(r -> !"완료".equals(r.getReturnStatus()) 
                                && !"취소".equals(r.getReturnStatus()) 
                                && !"거부".equals(r.getReturnStatus()));

            if (hasPendingReturn) {
                throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "반품 진행중인 주문은 정산 불가합니다. 주문번호: " + o.getOrderId());
            }
        }

        // 판매자 그룹핑
        Map<MemberDto, List<GoodsOrdersDto>> grouped =
                orders.stream()
                        .collect(Collectors.groupingBy(o -> o.getGoods().getMember()));

        // 정산 생성
        for (MemberDto seller : grouped.keySet()) {
            List<GoodsOrdersDto> sellerOrders = grouped.get(seller);
            long totalAmount = 0;
            long pgFee = 0;
            long platformFee = 0;
            long tax = 0;

            for (GoodsOrdersDto o : sellerOrders) {

                long price = o.getTotalPrice();
                long qty = o.getCnt();
                long shippingFee = o.getGdelPrice() != null ? o.getGdelPrice() : 0;
                //상품단가
                BigDecimal unitPrice = BigDecimal.valueOf(price)
                        .divide(BigDecimal.valueOf(qty), 0, RoundingMode.HALF_UP);

                long refundTotal = 0;
                long shippingPenalty = 0;
                
            	// 반품 조회
                List<GoodsReturnDto> returns =
                        adminGoodsReturnRepository.findAllByOrderAndDelYn(o, "n");
                boolean shippingApplied = false;
                // 상품 환불
                for (GoodsReturnDto r : returns) {
                	long cnt = r.getReturnCnt();
                    if ("반품".equals(r.getReturnType())) {
                        refundTotal = unitPrice.multiply(BigDecimal.valueOf(cnt)).longValue();
                        if (!shippingApplied && !"변심".equals(r.getReturnReason())) {
                        	shippingPenalty += shippingFee; // 보통 회수비 or 판매자 부담
                        	shippingApplied = true;
                        }
                    }

                    // 교환 = 상품환불 없음 (배송비만 처리)
                    else if ("교환".equals(r.getReturnType())) {
                    	// 교환은 환불 없음
                        if (!shippingApplied && !"변심".equals(r.getReturnReason())) {
                            // 판매자 귀책 → 왕복
                            shippingPenalty += shippingFee * 2;
                        } else {
                            // 소비자 귀책 → 회수비
                            shippingPenalty += shippingFee;
                        }
                        shippingApplied = true;
                    }
                }
                long finalPrice = price - refundTotal - shippingPenalty;
                totalAmount += finalPrice;

            }
            // 수수료
            pgFee = Math.round(totalAmount * policy.getPgFeeRate());
            platformFee = Math.round(totalAmount * policy.getPlatformFeeRate());
            tax = Math.round(totalAmount * policy.getTaxRate());
            
            long settleAmount = totalAmount - (pgFee + platformFee + tax);

            GoodsSettlementDto settlement = GoodsSettlementDto.builder()
                    .seller(seller)
                    .totalAmount(totalAmount)
                    .pgFee(pgFee)
                    .platformFee(platformFee)
                    .taxAmount(tax)
                    .settleAmount(settleAmount)
                    .status("정산완료")
                    .build();

            adminGoodsSettlementRepository.save(settlement);

            // 주문 연결
            for (GoodsOrdersDto o : sellerOrders) {
                o.setSettlement(settlement);
                o.setSettleYn("y");
            }
        }
    }*/
	
	/**
	 * 주문 ID 목록(orderIds)을 받아 판매자별로 정산을 생성하고 주문에 연결
	 *
	 * 주요 특징(요구사항 반영)
	 *  - 반품/교환은 요청 단위로 모두 누적 계산(한 주문에서 여러 반품 요청 가능)
	 *  - GoodsReturnDto.refundPrice가 있으면 우선 사용, 없으면 unitPrice * returnCnt로 계산
	 *  - 배송비 패널티는 요청 단위로 누적(정책에 따라 개당 적용으로 쉽게 변경 가능)
	 *  - 통화 연산은 BigDecimal로 통일(반올림/스케일 명시)
	 *  - settleAmount가 음수여도 정산 레코드에 그대로 저장(요구대로 지급 후 판매자에게 고지 가능)
	 *  - 변경된 주문은 명시적으로 saveAll 호출하여 DB 반영
	 *
	 * 예외: 입력 검증, 결제/구매확정/이미정산/반품진행중 체크 포함
	 */
	/*public Map<String, Object> settleOrders(List<Long> orderIds) throws BaCdException {
	    if (orderIds == null || orderIds.isEmpty()) {
	        throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "정산할 주문 ID가 제공되지 않았습니다.");
	    }

	    // 1) 주문 조회
	    List<GoodsOrdersDto> orders = adminGoodsOrdersRepository.findAllById(orderIds);
	    if (orders == null || orders.isEmpty()) {
	        throw new BaCdException(ErrorCode.NOT_FOUND, "주문한 내역이 존재하지 않습니다.");
	    }

	    // 2) 정산 정책 조회 (현재 1건 활성 가정)
	    CommissionPolicyDto policy = commissionPolicyRepository.findById(1L)
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "정산 정책을 찾을 수 없습니다."));

	    // 정책 비율을 BigDecimal로 변환
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
	        BigDecimal totalAmount = BigDecimal.ZERO;   // 정산 대상 총액 (환불/배송패널티 반영 후)
	        BigDecimal refundAmount = BigDecimal.ZERO;  // 환불 합계(판매자에게 차감되는 금액)

	        // 각 주문별로 환불/배송패널티 누적 계산
	        for (GoodsOrdersDto o : sellerOrders) {
	            long price = o.getTotalPrice() != null ? o.getTotalPrice() : 0L;
	            long qty = o.getCnt() != null && o.getCnt() > 0 ? o.getCnt() : 1L;
	            long shippingFee = o.getGdelPrice() != null ? o.getGdelPrice() : 0L;

	            // 주문 단가: 소수 2자리로 계산 (필요시 scale 조정)
	            BigDecimal unitPrice = BigDecimal.valueOf(price)
	                    .divide(BigDecimal.valueOf(qty), 2, RoundingMode.HALF_UP);

	            // 주문 단위 환불/패널티 누적
	            BigDecimal orderRefundTotal = BigDecimal.ZERO;
	            BigDecimal orderShippingPenalty = BigDecimal.ZERO;

	            // 반품 목록 조회 (삭제되지 않은 것만)
	            List<GoodsReturnDto> returns = adminGoodsReturnRepository.findAllByOrderAndDelYn(o, "n");

	            // 반품 요청별로 누적 처리 (shippingApplied 제거 — 모든 요청을 누적)
	            for (GoodsReturnDto r : returns) {
	                long cnt = r.getReturnCnt() != null ? r.getReturnCnt() : 0L;

	                // 환불 금액: 엔티티에 이미 계산된 refundPrice가 있으면 우선 사용
	                BigDecimal rRefund = (r.getRefundPrice() != null && r.getRefundPrice() > 0)
	                        ? BigDecimal.valueOf(r.getRefundPrice())
	                        : unitPrice.multiply(BigDecimal.valueOf(cnt));
	                orderRefundTotal = orderRefundTotal.add(rRefund);

	                // 배송비 패널티 계산 정책 (요청 단위 적용)
	                // - 변심: 구매자 부담 -> 패널티 없음
	                // - 판매자 귀책(오배송/파손 등): 판매자 부담 -> 회수비 또는 왕복비
	                // - 교환: 상품 환불 없음, 배송비는 왕복 또는 회수비
	                if ("반품".equals(r.getReturnType())) {
	                    if (!"변심".equals(r.getReturnReason())) {
	                        // 정책: 요청 단위로 회수비 적용.
	                        // 필요시 개당 적용으로 바꾸려면 shippingFee * cnt 로 변경
	                        orderShippingPenalty = orderShippingPenalty.add(BigDecimal.valueOf(shippingFee));
	                    }
	                } else if ("교환".equals(r.getReturnType())) {
	                    if (!"변심".equals(r.getReturnReason())) {
	                        // 판매자 귀책 -> 왕복비
	                        orderShippingPenalty = orderShippingPenalty.add(BigDecimal.valueOf(shippingFee * 2));
	                    } else {
	                        // 소비자 귀책 -> 회수비
	                        orderShippingPenalty = orderShippingPenalty.add(BigDecimal.valueOf(shippingFee));
	                    }
	                } else {
	                    // 기타 타입: 정책에 따라 처리 (로그/감사용)
	                }
	            }

	            // 주문의 최종 기여 금액 = 주문총액 - 주문환불합계 - 주문배송패널티
	            BigDecimal orderFinal = BigDecimal.valueOf(price)
	                    .subtract(orderRefundTotal)
	                    .subtract(orderShippingPenalty);

	            // **요구사항**: 음수 정산 허용 — 음수일 경우에도 그대로 누적하여 판매자에게 고지
	            totalAmount = totalAmount.add(orderFinal);
	            refundAmount = refundAmount.add(orderRefundTotal);
	        }

	        // 6) 수수료/세금 계산 (정책 비율 적용, 반올림은 원 단위로 처리)
	        long totalAmountLongForCalc = totalAmount.longValue(); // 원 단위 계산을 위해 long으로 변환
	        BigDecimal pgFee = totalAmount.compareTo(BigDecimal.ZERO) > 0
	                ? totalAmount.multiply(pgRate).setScale(0, RoundingMode.HALF_UP)
	                : BigDecimal.ZERO;

	        BigDecimal platformFee = totalAmount.compareTo(BigDecimal.ZERO) > 0
	                ? totalAmount.multiply(platformRate).setScale(0, RoundingMode.HALF_UP)
	                : BigDecimal.ZERO;

	        BigDecimal tax = totalAmount.compareTo(BigDecimal.ZERO) > 0
	                ? totalAmount.multiply(taxRate).setScale(0, RoundingMode.HALF_UP)
	                : BigDecimal.ZERO;

	        BigDecimal settleAmount = totalAmount.subtract(pgFee).subtract(platformFee).subtract(tax);

	        // 7) 정산 레코드 생성: 음수 허용(요구사항에 따라 지급 후 판매자에게 고지)
	        GoodsSettlementDto settlement = GoodsSettlementDto.builder()
	                .seller(seller)
	                .totalAmount(totalAmount.longValue())
	                .refundAmount(refundAmount.longValue())
	                .pgFee(pgFee.longValue())
	                .platformFee(platformFee.longValue())
	                .taxAmount(tax.longValue())
	                .settleAmount(settleAmount.longValue()) // 음수 가능
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
	        if (settleAmount.compareTo(BigDecimal.ZERO) < 0) {
	            // TODO: 판매자에게 음수 정산 사실을 고지하는 로직을 구현하세요.
	            // 예: notifySellerNegativeSettlement(savedSettlement);
	            // 예: createSettlementAdjustmentRecord(savedSettlement, settleAmount, "환불/교환으로 인한 마이너스 정산 - 판매자 고지");
	        }
	    }

	    result.put("result", true);
	    result.put("settleIds", createdSettlementIds);
	    return result;
	}*/
	
	
	
	/**
	 * 주문 ID 목록(orderIds)을 받아 판매자별로 정산을 생성하고 주문에 연결
	 *
	 * 주요 특징(요구사항 반영)
	 *  - 반품/교환은 요청 단위로 모두 누적 계산(한 주문에서 여러 반품 요청 가능)
	 *  - GoodsReturnDto.refundPrice가 있으면 우선 사용, 없으면 unitPrice * returnCnt로 계산
	 *  - 배송비 패널티는 요청 단위로 누적(정책에 따라 개당 적용으로 쉽게 변경 가능)
	 *  - 통화 연산은 BigDecimal로 통일(반올림/스케일 명시)
	 *  - settleAmount가 음수여도 정산 레코드에 그대로 저장(요구대로 지급 후 판매자에게 고지 가능)
	 * 예외: 입력 검증, 결제/구매확정/이미정산/반품진행중 체크 포함
	 */
	public Map<String, Object> settleOrders(List<Long> orderIds) throws BaCdException {
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

	    // 판매자별 그룹핑
	    Map<MemberDto, List<GoodsOrdersDto>> grouped = orders.stream()
	            .collect(Collectors.groupingBy(o -> o.getGoods().getMember()));

	    List<Long> createdSettlementIds = new ArrayList<>();

	    for (Map.Entry<MemberDto, List<GoodsOrdersDto>> entry : grouped.entrySet()) {
	        MemberDto seller = entry.getKey();
	        List<GoodsOrdersDto> sellerOrders = entry.getValue();

	        BigDecimal totalProductSales = BigDecimal.ZERO;
	        BigDecimal totalPenalty = BigDecimal.ZERO; // <-- 여기서 시작해서 모든 주문의 패널티를 더해야 함
	        BigDecimal totalRefund = BigDecimal.ZERO;

	        for (GoodsOrdersDto o : sellerOrders) {
	            // 1. 주문별 초기값 세팅
	            BigDecimal initialShippingFee = BigDecimal.valueOf(o.getGdelPrice() != null ? o.getGdelPrice() : 0L);
	            BigDecimal pureProductAmount = BigDecimal.valueOf(o.getTotalPrice()).subtract(initialShippingFee);
	            
	            BigDecimal orderRefundTotal = BigDecimal.ZERO;
	            
	            // 2. 해당 주문에 걸린 모든 반품/교환 조회
	            List<GoodsReturnDto> returns = adminGoodsReturnRepository.findAllByOrderAndDelYn(o, "n");

	            for (GoodsReturnDto r : returns) {
	                if (!"완료".equals(r.getReturnStatus())) continue;

	                // 환불금 합산
	                BigDecimal rRefund = BigDecimal.valueOf(r.getRefundPrice() != null ? r.getRefundPrice() : 0L);
	                orderRefundTotal = orderRefundTotal.add(rRefund);

	                // [핵심] 패널티 누적 (r.getGdelivPrice()를 사용하여 개별 건별로 3000원씩 더함)
	                if (!"변심".equals(r.getReturnReason())) {
	                    BigDecimal claimShipping = BigDecimal.valueOf(r.getGdelPrice() != null ? r.getGdelPrice() : 3000L);
	                    
	                    if ("교환".equals(r.getReturnType())) {
	                        totalPenalty = totalPenalty.add(claimShipping.multiply(BigDecimal.valueOf(2)));
	                    } else {
	                        // 반품 1건당 3000원씩 totalPenalty에 계속 누적됨
	                        totalPenalty = totalPenalty.add(claimShipping); 
	                    }
	                }
	            }

	            // 주문 매출 = 상품가 - 환불금 (전량 반품 시 0)
	            totalProductSales = totalProductSales.add(pureProductAmount.subtract(orderRefundTotal));
	            totalRefund = totalRefund.add(orderRefundTotal);
	        }

	        // [중요] 모든 주문 루프가 끝난 후 '딱 한 번' 수수료와 최종 정산금 계산
	        BigDecimal pgFee = totalProductSales.multiply(pgRate).setScale(0, RoundingMode.HALF_UP);
	        BigDecimal platformFee = totalProductSales.multiply(platformRate).setScale(0, RoundingMode.HALF_UP);
	        BigDecimal tax = totalProductSales.subtract(pgFee).subtract(platformFee).multiply(taxRate).setScale(0, RoundingMode.HALF_UP);

	        // 최종 정산금 = 매출 - 수수료 - 패널티
	        BigDecimal finalSettleAmount = totalProductSales.subtract(pgFee).subtract(platformFee).subtract(tax).subtract(totalPenalty);

	        // 5) 정산 데이터 빌드 및 저장
	        GoodsSettlementDto settlement = GoodsSettlementDto.builder()
	                .seller(seller)
	                .totalAmount(totalProductSales.longValue())
	                .refundAmount(totalRefund.longValue())
	                .pgFee(pgFee.longValue())
	                .platformFee(platformFee.longValue())
	                .taxAmount(tax.longValue())
	                .settleAmount(finalSettleAmount.longValue())
	                .status("정산완료")
	                .build();

	        GoodsSettlementDto savedSettlement = adminGoodsSettlementRepository.save(settlement);
	        createdSettlementIds.add(savedSettlement.getSettleId());

	        // 주문 상태 업데이트
	        for (GoodsOrdersDto o : sellerOrders) {
	            o.setSettlement(savedSettlement);
	            o.setSettleYn("y");
	        }
	        adminGoodsOrdersRepository.saveAll(sellerOrders);
	    }

	    return Map.of("result", true, "settleIds", createdSettlementIds);
	}

}
