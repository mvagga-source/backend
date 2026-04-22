package com.project.app.goodsReturn.service;

import java.net.http.HttpHeaders;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goods.repository.GoodsRepository;
import com.project.app.goodsReturn.dto.GoodsReturnDto;
import com.project.app.goodsReturn.repository.GoodsReturnRepository;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsorders.repository.GoodsOrdersRepository;
import com.project.app.notification.dto.NotificationDto;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsReturnServiceImpl implements GoodsReturnService {

    @Autowired GoodsOrdersRepository goodsOrdersRepository;
    
    @Autowired GoodsRepository goodsRepository; // 상품 재고 확인용
    
    @Autowired GoodsReturnRepository goodsReturnRepository;
    
    @Autowired
	private ApplicationEventPublisher eventPublisher;
    
    @Autowired
	private HttpSession session;
    
    @Value("${kakao.pay.api.secret-key}") // 설정 파일의 값을 주입
    String apiSecretKey;
    
    @Override
    public Map<String, Object> list(Map<String, Object> param) {
        //String memberId = (String) param.get("memberId");
        MemberDto member = Common.idCheck(session);
        int page = param.get("page") != null ? (int) param.get("page") - 1 : 0;
        int size = param.get("size") != null ? (int) param.get("size") : 10;
        
        // 날짜 처리 (문자열 "YYYY-MM-DD" -> Timestamp)
        Timestamp start = Timestamp.valueOf(param.get("startDate") + " 00:00:00");
        Timestamp end = Timestamp.valueOf(param.get("endDate") + " 23:59:59");

        Pageable pageable = PageRequest.of(page, size);
        Page<GoodsReturnDto> result = goodsReturnRepository.findMyReturnList(member.getId(), start, end, pageable);

        // React UI 구조에 맞춘 반환
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getContent());
        response.put("totalCount", result.getTotalElements());
        response.put("maxPage", result.getTotalPages());
        
        // 페이징 블록 계산 (예: 1~5, 6~10)
        int blockLimit = 5;
        int startPage = (((int) Math.ceil((double) (page + 1) / blockLimit)) - 1) * blockLimit + 1;
        int endPage = Math.min(startPage + blockLimit - 1, result.getTotalPages());

        response.put("startPage", startPage);
        response.put("endPage", endPage == 0 ? 1 : endPage);

        return response;
    }
    
    @Override
    public Map<String, Object> findSellerReturnList(Map<String, Object> param) throws BaCdException {
        //String memberId = (String) param.get("memberId");
        MemberDto member = Common.idCheck(session);
        int page = param.get("page") != null ? (int) param.get("page") - 1 : 0;
        int size = param.get("size") != null ? (int) param.get("size") : 10;
        
        // 날짜 처리 (문자열 "YYYY-MM-DD" -> Timestamp)
        Timestamp start = Timestamp.valueOf(param.get("startDate") + " 00:00:00");
        Timestamp end = Timestamp.valueOf(param.get("endDate") + " 23:59:59");

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> result = goodsReturnRepository.findSellerReturnList(member.getId(), start, end, pageable);

        // React UI 구조에 맞춘 반환
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getContent());
        response.put("totalCount", result.getTotalElements());
        response.put("maxPage", result.getTotalPages());
        
        // 페이징 블록 계산 (예: 1~5, 6~10)
        int blockLimit = 5;
        int startPage = (((int) Math.ceil((double) (page + 1) / blockLimit)) - 1) * blockLimit + 1;
        int endPage = Math.min(startPage + blockLimit - 1, result.getTotalPages());

        response.put("startPage", startPage);
        response.put("endPage", endPage == 0 ? 1 : endPage);

        return response;
    }
    
    @Override
	public Map<String, Object> findByGono(Long gono, MemberDto memberDto) throws BaCdException {
		// gono로 조회하되, 삭제되지 않은('n') 주문인지 추가 검증
		GoodsOrdersDto order = goodsOrdersRepository.findById(gono).filter(o -> "n".equals(o.getDelYn())).orElseThrow(() -> new BaCdException(ErrorCode.PAGE_NOT_FOUND, "주문 정보를 찾을 수 없습니다."));
		// 본인 주문인지 확인
        if(!order.getMember().getId().equals(memberDto.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER);
        }
        Long alreadyReturned = goodsReturnRepository.sumReturnCntByGono(order.getGono());

        // 반환 구조 (Controller가 Map을 기대한다면 아래처럼, 아니면 객체 그대로 반환)
        Map<String, Object> result = new HashMap<>();
        result.put("data", order);
		result.put("alreadyReturned", alreadyReturned);
        return result;
	}
    
    @Override
    public Map<String, Object> view(Long rno, MemberDto member) throws BaCdException {
    	// rno로 조회하되, 삭제되지 않은('n') 주문인지 추가 검증
    	GoodsReturnDto orderReturn = goodsReturnRepository.findById(rno).filter(o -> "n".equals(o.getDelYn())).orElseThrow(() -> new BaCdException(ErrorCode.PAGE_NOT_FOUND, "반품 정보를 찾을 수 없습니다."));
    	// 본인 주문인지 확인
    	if(!orderReturn.getMember().getId().equals(member.getId())) {
    		throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER_RETURN);
    	}
    	
    	// 반환 구조 (Controller가 Map을 기대한다면 아래처럼, 아니면 객체 그대로 반환)
    	Map<String, Object> result = new HashMap<>();
    	result.put("data", orderReturn);
    	return result;
    }
    
    @Override
	public Map<String, Object> findById(Long rno, MemberDto member) throws BaCdException {
    	Map<String, Object> result = new HashMap<>();
    	GoodsReturnDto goodsReturn = goodsReturnRepository.findByRnoAndDelYn(rno, "n").orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));
    	if(!goodsReturn.getOrder().getGoods().getMember().getId().equals(member.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_SALER);
        }
    	result.put("data", goodsReturn);
		return result;
	}

	@Override
	@Transactional
	public GoodsReturnDto requestReturn(GoodsReturnDto dto, MemberDto member) throws BaCdException {
	    // 1. 기존 주문 정보 조회 (배송비 정보 등이 이미 기록되어 있음)
	    GoodsOrdersDto order = goodsOrdersRepository.findById(dto.getOrder().getGono())
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

	    // 2. 권한 및 상태 검증
	    if (!order.getMember().getId().equals(member.getId())) throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER);
	    if("구매확정".equals(order.getDelivStatus())) {
	    	throw new BaCdException(ErrorCode.INPUT_EMPTY, "구매확정 상태에서는 반품이 불가능합니다.");
	    } else if (!"배송완료".equals(order.getDelivStatus())) {
	        throw new BaCdException(ErrorCode.INPUT_EMPTY, "배송완료 상태에서만 반품 신청이 가능합니다.");
	    }
	    
	    // 중복 및 초과 수량 체크
	    // 이미 반품 처리된(또는 진행중인) 수량 합계 조회
	    Long alreadyReturned = goodsReturnRepository.sumReturnCntByGono(order.getGono());
	    if (alreadyReturned == null) alreadyReturned = 0L;

	    Long requestingQty = dto.getReturnCnt(); // 사용자가 신청한 수량
	    Long totalOrderQty = order.getCnt(); // 총 주문 수량

	    if (alreadyReturned + requestingQty > totalOrderQty) {
	        throw new BaCdException(ErrorCode.IS_STATUS, "반품 가능한 수량을 초과했습니다. (잔여: " + (totalOrderQty - alreadyReturned) + "개)");
	    }

	    // 환불 금액 서버에서 재계산 (클라이언트 값 무시)
	    long itemPrice = order.getGoods().getPrice();
	    long refundPrice = itemPrice * requestingQty; // 기본 환불금액 = 단가 * 신청수량

	    // 배송비 정책 적용 (변심일 때만 왕복/편도 배송비 차감)
	    if ("변심".equals(dto.getReturnReason())) {
	        long deliveryFee = order.getGdelPrice() != null ? order.getGdelPrice() : order.getGoods().getGdelPrice();
	        refundPrice = refundPrice - deliveryFee;
	        if (refundPrice < 0) refundPrice = 0; // 마이너스 방지
	    }

	    // 3. 굿즈 정보 (최신 반품 주소지 획득용)
	    GoodsDto goods = order.getGoods();
	    
	    dto.setMember(member);

	    // 4. 스냅샷 데이터 채우기 (신청 시점의 정보 고정)
	    dto.setOrder(order);
	    dto.setReturnStatus("접수");
	    dto.setDelivStatus("배송대기");
	    
	    // 배송비 및 주소 스냅샷 (주문 시 기록된 값 우선, 없으면 상품 기본값)
	    dto.setGdelPrice(order.getGdelPrice() != null ? order.getGdelPrice() : goods.getGdelPrice());
	    dto.setGdelType(order.getGdelType() != null ? order.getGdelType() : goods.getGdelType());
	    dto.setGdelivAddr(order.getGdelivAddr() != null ? order.getGdelivAddr() : goods.getGdelivAddr());
	    dto.setGdelivAddrReturn(goods.getGdelivAddrReturn()); // 반품지는 상품정보 기준
	    dto.setGdelivAddrReturnDetail(goods.getGdelivAddrReturnDetail());

	    // 6. 반품 테이블 Insert
	    GoodsReturnDto result = goodsReturnRepository.save(dto);
	    
	    NotificationDto eventData = NotificationDto.builder()
                .member(goods.getMember())
                .sender(member)
                .nocontent(member.getNickname()+"님이'" + goods.getGname() + "' 상품에 반품 신청을 하셨습니다.")
                .type("GOODS_REVIEW")
                .url("/GoodsSaleReturn/" + dto.getRno())
                .isRead("n")
                .build();
        eventPublisher.publishEvent(eventData);
        
        return result;
	}
	
	
	@Override
	@Transactional
	public GoodsReturnDto update(GoodsReturnDto dto, MemberDto member) throws BaCdException {
		// 1. 기존 반품 신청 내역 존재 여부 확인
	    GoodsReturnDto returnDto = goodsReturnRepository.findById(dto.getRno())
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "수정할 신청 내역을 찾을 수 없습니다."));
	    
	    // 상태 검증: '접수'가 아니면 수정 절대 불가
	    if (!"접수".equals(returnDto.getReturnStatus())) {
	        throw new BaCdException(ErrorCode.IS_STATUS, "반품 처리가 시작되어 내용을 수정할 수 없습니다.");
	    }
		
		// 2. 권한 및 상태 검증
		if (!returnDto.getMember().getId().equals(member.getId())) throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER);
		if (!"배송완료".equals(returnDto.getOrder().getDelivStatus())) {
			throw new BaCdException(ErrorCode.INPUT_EMPTY, "배송완료 상태에서만 반품 신청이 가능합니다.");
		} else if("구매확정".equals(returnDto.getOrder().getDelivStatus())) {
			throw new BaCdException(ErrorCode.INPUT_EMPTY, "구매확정 상태에서는 반품이 불가능합니다.");
		}
		
		// 중복 및 초과 수량 체크
		// 이미 반품 처리된(또는 진행중인) 수량 합계 조회
		GoodsOrdersDto order = returnDto.getOrder();
		
		// 이미 반품 처리된 총 수량에서 '현재 수정 중인 이 건의 기존 수량'을 빼야 정확한 잔여 수량이 계산됨
	    Long totalAlreadyReturned = goodsReturnRepository.sumReturnCntByGono(order.getGono());
	    if (totalAlreadyReturned == null) totalAlreadyReturned = 0L;
	    
	    long otherReturnQty = totalAlreadyReturned - returnDto.getReturnCnt(); // 이 건을 제외한 다른 반품 건들의 수량 합계
	    Long requestingQty = returnDto.getReturnCnt(); // 새로 수정하려는 수량
	    Long totalOrderQty = order.getCnt(); // 총 주문 수량
	    
	    if (otherReturnQty + requestingQty > totalOrderQty) {
	        throw new BaCdException(ErrorCode.IS_STATUS, "반품 가능한 수량을 초과했습니다. (수정 가능 최대: " + (totalOrderQty - otherReturnQty) + "개)");
	    }
		
		// 환불 금액 서버에서 재계산 (클라이언트 값 무시)
		/*long itemPrice = order.getGoods().getPrice();
		long refundPrice = itemPrice * requestingQty; // 기본 환불금액 = 단가 * 신청수량
		
		// 배송비 정책 적용 (변심일 때만 왕복/편도 배송비 차감)
		if ("변심".equals(dto.getReturnReason())) {
			long deliveryFee = order.getGdelPrice() != null ? order.getGdelPrice() : order.getGoods().getGdelPrice();
			refundPrice = refundPrice - deliveryFee;
			if (refundPrice < 0) refundPrice = 0; // 마이너스 방지
		}*/
		
		// 3. 굿즈 정보 (최신 반품 주소지 획득용)
		GoodsDto goods = order.getGoods();
		
		returnDto.setMember(member);
		
		// 4. 스냅샷 데이터 채우기 (신청 시점의 정보 고정)
		returnDto.setOrder(order);
		returnDto.setReturnStatus("접수");
		returnDto.setReturnType(dto.getReturnType());
	    returnDto.setReturnReason(dto.getReturnReason());
	    returnDto.setReturnReasonDetail(dto.getReturnReasonDetail());
	    
	    // 수거지 및 요청사항 업데이트
	    returnDto.setPickupName(dto.getPickupName());
	    returnDto.setPickupPhone(dto.getPickupPhone());
	    returnDto.setPickupAddr(dto.getPickupAddr());
	    returnDto.setPickupAddrDetail(dto.getPickupAddrDetail());
	    returnDto.setOrderRequest(dto.getOrderRequest());
		
		// 배송비 및 주소 스냅샷 (주문 시 기록된 값 우선, 없으면 상품 기본값)
		returnDto.setGdelPrice(returnDto.getGdelPrice() != null ? returnDto.getGdelPrice() : goods.getGdelPrice());
		returnDto.setGdelType(returnDto.getGdelType() != null ? returnDto.getGdelType() : goods.getGdelType());
		returnDto.setGdelivAddr(returnDto.getGdelivAddr() != null ? returnDto.getGdelivAddr() : goods.getGdelivAddr());
		returnDto.setGdelivAddrReturn(returnDto.getGdelivAddrReturn()); // 반품지는 상품정보 기준
		returnDto.setGdelivAddrReturnDetail(returnDto.getGdelivAddrReturnDetail());
		
		GoodsReturnDto result = goodsReturnRepository.save(returnDto);
		
		NotificationDto eventData = NotificationDto.builder()
                .member(goods.getMember())
                .sender(member)
                .nocontent(member.getNickname()+"님이'" + goods.getGname() + "' 상품에 반품 신청을 수정하셨습니다.")
                .type("GOODS_REVIEW")
                .url("/GoodsSaleReturn/" + dto.getRno())
                .isRead("n")
                .build();
        eventPublisher.publishEvent(eventData);
        
		return result;
	}
	
	@Transactional
	public GoodsReturnDto updateStatus(Long rno, String status, Long gdelPrice, String gdelType, String returnSaleReasonDetail, MemberDto member) throws BaCdException {
	    // 1. 기존 반품 신청 정보 조회
	    GoodsReturnDto returnDto = goodsReturnRepository.findById(rno)
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "신청 내역을 찾을 수 없습니다."));

	    String currentStatus = returnDto.getReturnStatus();
	    
	    if (!returnDto.getOrder().getGoods().getMember().getId().equals(member.getId())) {
	        throw new BaCdException(ErrorCode.AUTH_USER_NOT_SALER);
	    }

	    // 2. 동일 상태 요청 방지
	    if (currentStatus.equals(status)) {
	        throw new BaCdException(ErrorCode.IS_STATUS, "이미 해당 상태입니다.");
	    }

	    // 3. 종료 상태 방어
	    if ("완료".equals(currentStatus) || "거부".equals(currentStatus) || "취소".equals(currentStatus)) {
	        throw new BaCdException(ErrorCode.COMPLETE_STATUS, "이미 처리 완료된 상태입니다.");
	    }

	    // 4. 상태 전이 검증 (핵심)
	    boolean valid = false;

	    if ("접수".equals(currentStatus)) {
	        valid = status.equals("회수중") || status.equals("거부");
	    } 
	    else if ("회수중".equals(currentStatus)) {
	        valid = status.equals("완료");
	    }

	    if (!valid) {
	        throw new BaCdException(ErrorCode.INVALID_PARAMETER, "허용되지 않은 상태 변경입니다.");
	    }

	    // 5. 거부 시 사유 필수
	    if ("거부".equals(status)) {
	        if (returnSaleReasonDetail == null || returnSaleReasonDetail.isBlank()) {
	            throw new BaCdException(ErrorCode.INPUT_EMPTY, "거부 사유를 입력해주세요.");
	        }
	    }
	    
	    // 2. 상태값 및 판매자 입력 정보 업데이트
	    returnDto.setReturnStatus(status);
	    if (gdelPrice != null) returnDto.setGdelPrice(gdelPrice);
	    if (gdelType != null) returnDto.setGdelType(gdelType);
	    if (returnSaleReasonDetail != null) returnDto.setReturnSaleReasonDetail(returnSaleReasonDetail);

	    // 3. '완료' 상태일 때의 특수 처리 (재고 복구 등)
	    if ("완료".equals(status)) {
	        // 교환이 아닌 '반품'인 경우에만 재고를 다시 채워줌
	        if ("반품".equals(returnDto.getReturnType())) {
	            //GoodsDto goods = returnDto.getOrder().getGoods();
	            // 주문했던 수량만큼 재고 플러스 (아직 반품된게 아니고 재고 수량은 불량인 경우도 있어서 굿즈상태가 멀쩡한거면 본인이 상품 수정페이지에서 재고 채우기)
	            // goods.setGstock(goods.getGstock() + returnDto.getReturnCnt());
	        }
	    }

	    GoodsReturnDto result = goodsReturnRepository.save(returnDto);
	    
	    // 2. 상태별 알림 메시지 설정
	    String gname = returnDto.getOrder().getGoods().getGname();
	    String msg = "";

	    switch (status) {
	        case "회수중":
	            msg = String.format("'%s' 상품의 수거가 시작되었습니다. 기사님 방문에 대비해 주세요.", gname);
	            break;
	        case "완료":
	            msg = String.format("'%s' 상품의 %s 처리가 최종 완료되었습니다.", gname, returnDto.getReturnType());
	            break;
	        case "거부":
	            msg = String.format("'%s' 상품의 신청이 거부되었습니다. 사유: %s", gname, returnSaleReasonDetail);
	            break;
	        case "취소":
	            msg = String.format("'%s' 상품의 반품/교환 신청이 취소되었습니다.", gname);
	            break;
	    }

	    // 3. 알림 이벤트 발행 (메시지가 설정된 경우에만)
	    if (!msg.isEmpty()) {
	        NotificationDto eventData = NotificationDto.builder()
	                .member(returnDto.getMember()) // 수신자: 구매자
	                .sender(member)                // 발신자: 판매자
	                .nocontent(msg)
	                .type("GOODS_TRADE")
	                .url("/MyMain/MyPurchaseReturn/" + returnDto.getRno()) // 구매자용 상세페이지 주소
	                .isRead("n")
	                .build();
	        
	        eventPublisher.publishEvent(eventData);
	    }
	    
	    return result;
	}

	@Override
	@Transactional
	public GoodsReturnDto delete(Long rno) throws BaCdException {
		// 반품 내역 조회
	    GoodsReturnDto returnDto = goodsReturnRepository.findById(rno).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

	    // 상태 검증: '접수' 상태일 때만 취소(삭제) 가능
	    // 만약 이미 '회수중'이거나 '완료'된 상태라면 취소할 수 없어야 함
	    if("거부".equals(returnDto.getReturnStatus())) {
	    	throw new BaCdException(ErrorCode.IS_STATUS, "이미 반품처리가 거부되어 반품 내역을 취소할 수 없습니다.");
	    } else if (!"접수".equals(returnDto.getReturnStatus())) {
	        throw new BaCdException(ErrorCode.IS_STATUS, "이미 반품처리가 시작된 반품 내역은 취소할 수 없습니다.");
	    }
	    //returnDto.setDelYn("y");
	    
	    //상태를 '취소'로 변경
	    returnDto.setReturnStatus("취소");
	    return goodsReturnRepository.save(returnDto);
	}
}