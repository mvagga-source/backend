package com.project.app.goodsorders.service;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        map.put("approval_url", "http://localhost:3000/Payment/Success");
        map.put("fail_url", "http://localhost:3000/Payment/Fail");
        map.put("cancel_url", "http://localhost:3000/Payment/Cancel");

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
	            goods.setStockCnt(goods.getStockCnt() - order.getCnt());
	            goodsOrdersRepository.save(order);	//저장
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
}