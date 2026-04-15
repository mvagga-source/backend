package com.project.app.goodsorders.service;

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
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goods.repository.GoodsRepository;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsorders.repository.GoodsOrdersRepository;
import com.project.app.notification.dto.NotificationDto;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsOrdersServiceImpl implements GoodsOrdersService {

    @Autowired GoodsOrdersRepository goodsOrdersRepository;
    
    @Autowired GoodsRepository goodsRepository; // 상품 재고 확인용
    
    @Autowired
	private ApplicationEventPublisher eventPublisher;
    
    @Value("${server.host}")
	private String hostUrl;
    
    @Value("${kakao.pay.api.secret-key}") // 설정 파일의 값을 주입
    String apiSecretKey;

    @Override
    @Transactional
    public GoodsOrdersDto createOrder(GoodsOrdersDto dto) throws BaCdException {
        // 1. 최신 상품 정보 조회 및 재고 체크
        GoodsDto goods = goodsRepository.findById(dto.getGoods().getGno())
                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

        if (goods.getStockCnt() < dto.getCnt()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "재고가 부족합니다.");
        }

        // 2. 총 가격 계산 (서버에서 다시 계산하여 위변조 방지)
        long totalPrice = goods.getPrice() * dto.getCnt();
        dto.setTotalPrice(totalPrice);
        
        // 3. 주문 저장
        return goodsOrdersRepository.save(dto);
    }

    @Override
    public Map<String, Object> findMyOrders(MemberDto member, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crdt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // 구매자 ID와 삭제되지 않은 주문 조회
        Page<GoodsOrdersDto> pageList = goodsOrdersRepository.findByMemberIdAndDelYn(member.getId(), "n", pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("list", pageList.getContent());
        map.put("page", page);
        map.put("maxPage", pageList.getTotalPages());
        
        int startPage = ((page - 1) /size) * size  + 1;
        int endPage = startPage + size - 1;
        if (endPage > pageList.getTotalPages()) endPage = pageList.getTotalPages();
        
        map.put("startPage", startPage);        
        map.put("endPage", endPage);                
        map.put("totalCount", pageList.getTotalElements());

        return map;
    }

    @Override
    public GoodsOrdersDto findByGono(GoodsOrdersDto dto) {
        return goodsOrdersRepository.findById(dto.getGono()).filter(o -> "n".equals(o.getDelYn())).orElse(null);
    }

    @Override
    @Transactional
    public void updateStatus(String orderId, String tid, String status) {
        // 결제 완료 시 호출될 로직
        GoodsOrdersDto order = goodsOrdersRepository.findByOrderId(orderId).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));
        
        order.setTid(tid);
        order.setStatus(status);
        
        // 결제 완료(PAID) 시 재고 차감
        if ("PAID".equals(status)) {
            GoodsDto goods = order.getGoods();
            goods.setStockCnt(goods.getStockCnt() - order.getCnt());
        }
    }

    @Override
	public GoodsOrdersDto findByGono(Long gono, MemberDto memberDto) throws BaCdException {
		// gono로 조회하되, 삭제되지 않은('n') 주문인지 추가 검증
		GoodsOrdersDto order = goodsOrdersRepository.findById(gono).filter(o -> "n".equals(o.getDelYn())).orElse(null);
		// 본인 주문인지 확인
        if(!order.getMember().getId().equals(memberDto.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER);
        }
        return order;
	}
    
    @Override
	public GoodsOrdersDto findByGonoSaler(Long gono, MemberDto memberDto) throws BaCdException {
		// gono로 조회하되, 삭제되지 않은('n') 주문인지 추가 검증
		GoodsOrdersDto order = goodsOrdersRepository.findById(gono).filter(o -> "n".equals(o.getDelYn())).orElse(null);
		// 본인 주문인지 확인
        if(!order.getGoods().getMember().getId().equals(memberDto.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_SALER);
        }
        return order;
	}

	@Override
    @Transactional
    public Map<String, Object> readyPayment(GoodsOrdersDto orderRequest, MemberDto member) throws BaCdException {
        // 가맹점 주문번호 생성 (예: ORD-20260324-uuid)
        String partnerOrderId = "ORD-" + System.currentTimeMillis();
        
        // 카카오페이 요청 데이터 구성
        Map<String, Object> map = new HashMap<>();
        map.put("cid", "TC0ONETIME");
        map.put("partner_order_id", partnerOrderId);
        map.put("partner_user_id", member.getId());
        map.put("item_name", orderRequest.getGoods().getGname()); // 상품명
        map.put("quantity", orderRequest.getCnt());
        map.put("total_amount", orderRequest.getTotalPrice());
        map.put("tax_free_amount", 0);
        
        // 리액트 환경에 맞춰 성공/실패 URL 설정
        // 리액트 라우터에서 처리할 경로
        map.put("approval_url", "http://"+hostUrl+":3000/Payment/Success");
        map.put("fail_url", "http://"+hostUrl+":3000/Payment/Fail");
        map.put("cancel_url", "http://"+hostUrl+":3000/Payment/Cancel");

        // 카카오 페이 전송 (WebClient)
        WebClient webClient = WebClient.create();
        Map<String, Object> responseDto = webClient.post()
                .uri("https://open-api.kakaopay.com/online/v1/payment/ready")
                .header("Authorization", "SECRET_KEY "+apiSecretKey) // 본인 키값 넣기
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // 주문 정보 임시 저장 (TID 포함)
        orderRequest.setOrderId(partnerOrderId);
        orderRequest.setTid(responseDto.get("tid").toString());
        orderRequest.setStatus("READY");		//주문대기상태  READY(대기), PAID(완료), CANCEL(취소), FAILED(실패)
        orderRequest.setPaymentMethod("KAKAO_PAY");
        orderRequest.setMember(member);
        //판매자가 수정할 수 있는 내용들 기록용으로 추가
        orderRequest.setGdelivAddr(orderRequest.getGoods().getGdelivAddr());
        orderRequest.setGdelivAddrReturn(orderRequest.getGoods().getGdelivAddrReturn());
        orderRequest.setGdelivAddrReturnDetail(orderRequest.getGoods().getGdelivAddrReturnDetail());
        orderRequest.setGdelType(orderRequest.getGoods().getGdelType());
        orderRequest.setGdelPrice(orderRequest.getGoods().getGdelPrice());
        orderRequest.setDelivStatus("배송대기");
        goodsOrdersRepository.save(orderRequest);
        return responseDto;
    }

	@Override
	public Map<String, Object> approvePayment(String pgToken, String tid, MemberDto memberDto) throws BaCdException {
		try {
			// DB에서 결제 대기 중인 주문 정보 조회 (TID 기준)
	        GoodsOrdersDto order = goodsOrdersRepository.findByTid(tid).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "주문 정보를 찾을 수 없습니다."));
	        
	        // 재고 차감
            GoodsDto goods = order.getGoods();
            if (goods.getStockCnt() < order.getCnt()) {
                throw new BaCdException(ErrorCode.KAKAO_PAY_EMPTY_STOCKCNT);		//재고가 소진되었는지 검사
            }
            
	        // 카카오페이 승인 요청 데이터 구성
	        Map<String, Object> map = new HashMap<String, Object>();
			map.put("aid", "A5678901234567890123");			//요청 고유 번호 - 승인/취소가 구분된 결제번호
			map.put("tid", tid);					//결제 고유번호, 결제 준비 API 응답에 포함
			map.put("cid", "TC0ONETIME");			//가맹점 코드, 10자
			map.put("partner_order_id", order.getOrderId());			//가맹점 주문번호, 최대 100자
			map.put("partner_user_id", memberDto.getId());			//가맹점 회원 id, 최대 100자(실명, 휴대폰번호, 이메일주소, ID와 같은 개인정보 전송 불가)
			map.put("pg_token", pgToken);		//상품명
			
			//카카오 페이로 전송
			WebClient webClient = WebClient.create();
			Map<String, Object> approveResponseDto = webClient.post()
					.uri("https://open-api.kakaopay.com/online/v1/payment/approve")
					.header("Authorization", "SECRET_KEY "+apiSecretKey)
					.header("Context-Type", "application/json")
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(map)
					.retrieve()
					.bodyToMono(Map.class)
					.block();
			
			// 결제 성공 시 DB 상태 업데이트 및 재고 차감
	        if (approveResponseDto != null) {
	            order.setStatus("PAID"); // 상태 변경 READY(대기), PAID(완료), CANCEL(취소), FAILED(실패)
	            order.setDelivStatus("배송대기");
	            goods.setStockCnt(goods.getStockCnt() - order.getCnt());
	            //주문 완료 후 개수가 0이면 자동 품절상태로 변경
	            if(goods.getStockCnt() == 0) {
		            goods.setStatus("품절");
	            }
	            goodsOrdersRepository.save(order);	//저장
	            
            	// [알림 로직] 판매자에게 "새로운 주문" 알림
	            MemberDto seller = goods.getMember(); // 상품 등록자
	            //if (seller != null && !seller.getId().equals(memberDto.getId())) {
	                NotificationDto sellerEvent = NotificationDto.builder()
	                        .member(seller)
	                        .sender(memberDto)
	                        .nocontent(memberDto.getNickname()+"님이 '" + goods.getGname() + "' 상품을 주문하셨습니다.")
	                        .type("GOODS_TRADE") // 설정에서 allowGoodsTrade로 체크됨
	                        .url("/MyMain/MySaleRecord") // 판매 내역 페이지
	                        .isRead("n")
	                        .build();
	                eventPublisher.publishEvent(sellerEvent);
	            //}
	        }
			
			return approveResponseDto;
	    } catch (BaCdException e) {
	        // 내가 의도적으로 던진 에러(재고 부족 등)는 그대로 밖으로 보냄
	        throw e;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new BaCdException(ErrorCode.KAKAO_PAY_APPROVE_ERROR);
	    }
	}
	
	/**
	 * 주문 취소 주문정보status를 CANCEL로 변경
	 * @param tid
	 * @param status
	 * @throws BaCdException
	 */
	@Override
	@Transactional
	public void failOrCancelPayment(String tid, String status) throws BaCdException {
	    // TID로 주문 찾기
	    GoodsOrdersDto order = goodsOrdersRepository.findByTid(tid).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "주문 정보를 찾을 수 없습니다."));

	    // 상태 업데이트 (CANCEL 또는 FAILED)
	    // 이미 PAID 상태인 주문을 취소/실패로 돌리지 않도록
	    if (!"PAID".equals(order.getStatus())) {
	        order.setStatus(status);
	        goodsOrdersRepository.save(order);
	    }
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> cancelOrder(Long gono, MemberDto memberDto) throws BaCdException {
	    // 주문 조회
	    GoodsOrdersDto order = goodsOrdersRepository.findById(gono)
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "주문 정보를 찾을 수 없습니다."));

	    // 권한 및 상태 체크 (배송 시작 전인 PAID 상태만 즉시 취소 가능)
	    if (!order.getMember().getId().equals(memberDto.getId())) throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER);
	    if (!"PAID".equals(order.getStatus())) throw new BaCdException(ErrorCode.INPUT_EMPTY, "취소 가능한 상태가 아닙니다.");
	    if (!"배송대기".equals(order.getDelivStatus())) throw new BaCdException(ErrorCode.INPUT_EMPTY, "이미 배송이 시작되어 취소가 불가합니다. 반품을 이용해주세요.");

	    // 카카오페이 취소 API 요청 데이터 구성
	    Map<String, Object> map = new HashMap<>();
	    map.put("cid", "TC0ONETIME");
	    map.put("tid", order.getTid());
	    map.put("cancel_amount", order.getTotalPrice()); // 전체 취소
	    map.put("cancel_tax_free_amount", 0);

	    // 카카오페이 취소 API 호출
	    WebClient webClient = WebClient.create();
	    Map<String, Object> cancelResponse = webClient.post()
	            .uri("https://open-api.kakaopay.com/online/v1/payment/cancel")
	            .header("Authorization", "SECRET_KEY " + apiSecretKey)
	            .contentType(MediaType.APPLICATION_JSON)
	            .bodyValue(map)
	            .retrieve()
	            .bodyToMono(Map.class)
	            .block();						//관리자가 결제취소 처리할 필요 없음

	    // DB 업데이트 (상태 변경 및 재고 복구)
	    order.setStatus("CANCEL");
	    order.setDelivStatus("취소완료");
	    order.setCancelDate(new Timestamp(System.currentTimeMillis()));
	    
	    // 재고 복구
	    GoodsDto goods = order.getGoods();
	    goods.setStockCnt(goods.getStockCnt() + order.getCnt());
	    if(goods.getStockCnt() > 0 && "품절".equals(goods.getStatus())) {			//동시에 다른 사용자가 취소하거나 동시에 발생할 수 있는 문제있음
	        goods.setStatus("판매중");
	    }
	    
	    goodsOrdersRepository.save(order);
	    
	    MemberDto seller = goods.getMember(); // 상품 등록자
        //if (seller != null && !seller.getId().equals(memberDto.getId())) {	//판매자인 경우
            NotificationDto sellerEvent = NotificationDto.builder()
                    .member(seller)
                    .sender(memberDto)
                    .nocontent(memberDto.getNickname()+"님이 '" + goods.getGname() + "' 상품 주문을 취소하셨습니다.")
                    .type("GOODS_TRADE") // 설정에서 allowGoodsTrade로 체크됨
                    .url("/MyMain/MySaleRecord") // 판매 내역 페이지
                    .isRead("n")
                    .build();
            eventPublisher.publishEvent(sellerEvent);
        //}
	    return cancelResponse;
	}
	
	@Override
	@Transactional
	public void prepareOrder(Long gono, MemberDto seller) throws BaCdException {
	    GoodsOrdersDto order = goodsOrdersRepository.findById(gono)
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

	    // 권한 체크: 주문된 상품의 판매자가 현재 로그인한 사용자인지 확인
	    if (!order.getGoods().getMember().getId().equals(seller.getId())) {
	        throw new BaCdException(ErrorCode.AUTH_USER_NOT_SALER); // 판매자 권한 없음
	    }

	    if (!"배송대기".equals(order.getDelivStatus())) {
	        throw new BaCdException(ErrorCode.IS_STATUS, "배송대기 상태인 주문만 처리가 가능합니다.");
	    }

	    order.setDelivStatus("배송준비중");
	    // 별도의 save 없이 @Transactional에 의해 변경 감지(Dirty Check)로 업데이트됨
	}

	@Override
	@Transactional
	public void startShipping(Long gono, String trackingNo, String gdelType, MemberDto seller) throws BaCdException {
	    GoodsOrdersDto order = goodsOrdersRepository.findById(gono)
	            .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

	    // 권한 체크
	    if (!order.getGoods().getMember().getId().equals(seller.getId())) {
	        throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER);
	    }

	    // 상태 변경 및 송장 번호 저장
	    order.setDelivStatus("배송중");
	    order.setTrackingNo(trackingNo); // DTO에 추가한 필드
	    if (gdelType != null) order.setGdelType(gdelType);
	    goodsOrdersRepository.save(order);

	    // [알림 로직] 구매자에게 "배송 시작" 알림 발송
	    NotificationDto buyerEvent = NotificationDto.builder()
	            .member(order.getMember()) // 구매자
	            .sender(seller) // 판매자
	            .nocontent("주문하신 상품 '" + order.getGoods().getGname() + "'의 배송이 시작되었습니다. (송장: " + trackingNo + ")")
	            .type("GOODS_TRADE")
	            .url("/MyMain/MyOrder") // 구매 내역 페이지
	            .isRead("n")
	            .build();
	    eventPublisher.publishEvent(buyerEvent);
	}
	
	@Override
    @Transactional
    public void updateDeliveryStatus(GoodsOrdersDto dto, MemberDto seller) throws BaCdException {
        // 1. 주문 조회
        GoodsOrdersDto order = goodsOrdersRepository.findById(dto.getGono())
                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

        // 2. 판매자 권한 체크: 상품 등록자와 로그인한 세션 유저가 같은지 확인
        if (!order.getGoods().getMember().getId().equals(seller.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER);
        }

        // 3. 상태별 로직 처리
        String targetStatus = dto.getDelivStatus();
        
        if ("배송준비중".equals(targetStatus)) {
            // 발주 확인 단계
            order.setDelivStatus("배송준비중");
        } 
        else if ("배송중".equals(targetStatus)) {
            // 송장 등록 단계
            if (dto.getTrackingNo() == null || dto.getTrackingNo().isEmpty()) {
                throw new BaCdException(ErrorCode.INPUT_EMPTY, "운송장 번호가 없습니다.");
            }
            order.setDelivStatus("배송중");
            order.setTrackingNo(dto.getTrackingNo());
            order.setGdelType(dto.getGdelType() != null ? dto.getGdelType() : "일반택배");
            
            // 구매자에게 배송 시작 알림 발송
            eventPublisher.publishEvent(NotificationDto.builder()
                    .member(order.getMember()) // 수신자: 구매자
                    .sender(seller)            // 발신자: 판매자
                    .nocontent("주문하신 '" + order.getGoods().getGname() + "' 상품의 배송이 시작되었습니다.")
                    .type("GOODS_TRADE")
                    .url("/MyMain/MyOrder")
                    .isRead("n")
                    .build());
        }
        // Dirty Checking으로 save 호출 없이 자동 업데이트
    }

    @Override
    @Transactional
    public void adminCancelOrder(Long gono, String reason, MemberDto seller) throws BaCdException {
        GoodsOrdersDto order = goodsOrdersRepository.findById(gono)
                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

        // 판매자 권한 체크
        if (!order.getGoods().getMember().getId().equals(seller.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_ORDER);
        }

        // 이미 배송 시작했으면 취소 불가
        if ("배송중".equals(order.getDelivStatus()) || "배송완료".equals(order.getDelivStatus())) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "이미 배송이 시작되어 취소할 수 없습니다.");
        }

        // 권한 및 상태 체크 (배송 시작 전인 PAID 상태만 즉시 취소 가능)
	    if (!"PAID".equals(order.getStatus())) throw new BaCdException(ErrorCode.INPUT_EMPTY, "취소 가능한 상태가 아닙니다.");
	    if (!"배송대기".equals(order.getDelivStatus())) throw new BaCdException(ErrorCode.INPUT_EMPTY, "이미 배송이 시작되어 취소가 불가합니다. 반품을 이용해주세요.");

	    // 카카오페이 취소 API 요청 데이터 구성
	    Map<String, Object> map = new HashMap<>();
	    map.put("cid", "TC0ONETIME");
	    map.put("tid", order.getTid());
	    map.put("cancel_amount", order.getTotalPrice()); // 전체 취소
	    map.put("cancel_tax_free_amount", 0);
	    
        // 카카오페이 환불 API 호출 (기존 cancelOrder 로직 재활용)
	    WebClient webClient = WebClient.create();
	    Map<String, Object> cancelResponse = webClient.post()
	            .uri("https://open-api.kakaopay.com/online/v1/payment/cancel")
	            .header("Authorization", "SECRET_KEY " + apiSecretKey)
	            .contentType(MediaType.APPLICATION_JSON)
	            .bodyValue(map)
	            .retrieve()
	            .bodyToMono(Map.class)
	            .block();

        order.setStatus("CANCEL");
        order.setDelivStatus("취소완료");
        order.setCancelReason(reason); // 사유 저장
        order.setCancelDate(new Timestamp(System.currentTimeMillis()));
        
        // 재고 복구
        GoodsDto goods = order.getGoods();
        goods.setStockCnt(goods.getStockCnt() + order.getCnt());
        if ("품절".equals(goods.getStatus())) goods.setStatus("판매중");

        // 구매자에게 취소 알림
        eventPublisher.publishEvent(NotificationDto.builder()
                .member(order.getMember())
                .nocontent("판매자 사정으로 주문이 취소되었습니다. 사유: " + reason)
                .type("GOODS_TRADE")
                .url("/MyMain/MyOrder")
                .isRead("n")
                .build());
    }
}