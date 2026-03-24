package com.project.app.goodsorders.service;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsOrdersServiceImpl implements GoodsOrdersService {

    @Autowired GoodsOrdersRepository goodsOrdersRepository;
    
    @Autowired GoodsRepository goodsRepository; // 상품 재고 확인용

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
        map.put("totalCount", pageList.getTotalElements());

        return map;
    }

    @Override
    public GoodsOrdersDto findByGono(GoodsOrdersDto dto) {
        return goodsOrdersRepository.findById(dto.getGono()).orElse(null);
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
	public GoodsOrdersDto findByGono(Long gono) throws BaCdException {
		return goodsOrdersRepository.findById(gono).orElse(null);
	}

	@Override
    @Transactional
    public Map<String, Object> readyPayment(GoodsOrdersDto orderRequest, MemberDto member) throws BaCdException {
        // 1. 가맹점 주문번호 생성 (예: ORD-20260324-uuid)
        String partnerOrderId = "ORD-" + System.currentTimeMillis();
        
        // 2. 카카오페이 요청 데이터 구성
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
        map.put("approval_url", "http://localhost:3000/payment/success");
        map.put("fail_url", "http://localhost:3000/payment/fail");
        map.put("cancel_url", "http://localhost:3000/payment/cancel");

        // 3. 카카오 페이 전송 (WebClient)
        WebClient webClient = WebClient.create();
        Map<String, Object> responseDto = webClient.post()
                .uri("https://open-api.kakaopay.com/online/v1/payment/ready")
                .header("Authorization", "SECRET_KEY DEV5AF16E011E90E89F908638FCB0AE957934741") // 본인 키값 넣기
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // 4. 주문 정보 임시 저장 (TID 포함)
        orderRequest.setOrderId(partnerOrderId);
        orderRequest.setTid(responseDto.get("tid").toString());
        orderRequest.setStatus("READY");
        orderRequest.setPaymentMethod("KAKAO_PAY");
        orderRequest.setMember(member);
        // member, goods 객체 등 매핑 로직 추가 필요
        goodsOrdersRepository.save(orderRequest);

        return responseDto;
    }

	@Override
	public Map<String, Object> approvePayment(String pgToken, String tid, MemberDto memberDto) throws BaCdException {
		try {
	        // 1. 카카오페이 승인 요청을 위한 헤더 설정
	        /*HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "SECRET_KEY " + "DEV5AF16E011E90E89F908638FCB0AE957934741"); // 내 애플리케이션의 Secret Key
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        // 2. 승인 요청 파라미터 (준비 단계와 데이터가 일치해야 함)
	        Map<String, Object> params = new HashMap<>();
	        params.put("cid", "TC0ONETIME"); // 가맹점 코드 (테스트용)
	        params.put("tid", tid);           // 세션에서 가져온 TID
	        params.put("partner_order_id", "ORD-123"); // 준비 때 쓴 ID와 동일해야 함 (DB 조회 필요)
	        params.put("partner_user_id", memberDto.getId());
	        params.put("pg_token", pgToken);

	        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

	        // 3. 카카오페이 서버로 승인 요청 전송
	        RestTemplate restTemplate = new RestTemplate();
	        Map<String, Object> response = restTemplate.postForObject(
	            "https://open-api.kakaopay.com/v1/payment/approve", 
	            request, 
	            Map.class
	        );

	        // 4. 결제가 성공했다면 DB 상태 업데이트
	        if (response != null) {
	            // DB에서 해당 tid로 주문 정보를 찾아서 상태를 PAID로 변경
	            GoodsOrdersDto order = goodsOrdersRepository.findByTid(tid)
	                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다."));
	            
	            order.setStatus("PAID");
	            goodsOrdersRepository.save(order);
	            
	            // 재고 차감 로직도 여기서 실행하면 좋음
	        }

	        return response;*/
	        
	        Map<String, Object> map = new HashMap<String, Object>();
			map.put("aid", "A5678901234567890123");			//요청 고유 번호 - 승인/취소가 구분된 결제번호
			map.put("tid", tid);					//결제 고유번호, 결제 준비 API 응답에 포함
			map.put("cid", "TC0ONETIME");			//가맹점 코드, 10자
			map.put("partner_order_id", "1234567890");			//가맹점 주문번호, 최대 100자
			map.put("partner_user_id", memberDto.getId());			//가맹점 회원 id, 최대 100자(실명, 휴대폰번호, 이메일주소, ID와 같은 개인정보 전송 불가)
			map.put("pg_token", pgToken);		//상품명
			
			//카카오 페이로 전송
			WebClient webClient = WebClient.create();
			Map<String, Object> approveResponseDto = webClient.post()
					.uri("https://open-api.kakaopay.com/online/v1/payment/approve")
					.header("Authorization", "SECRET_KEY DEV5AF16E011E90E89F908638FCB0AE957934741")
					.header("Context-Type", "application/json")
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(map)
					.retrieve()
					.bodyToMono(Map.class)
					.block();
			
			return approveResponseDto;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("카카오페이 승인 중 오류 발생");
	    }
	}
}