package com.project.app.goodsorders.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.goodsorders.service.GoodsOrdersService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/goodsOrders")
public class GoodsOrdersController {

    @Autowired
    GoodsOrdersService goodsOrdersService;

    @Autowired
    HttpSession session;
    
    /**
     * 주문 생성 (결제 준비 단계)
     */
    @ResponseBody
    @PostMapping("/ready")
    public AjaxResponse orderPay(@RequestBody GoodsOrdersDto odto) {
    	MemberDto memberDto = Common.idCheck(session);
    	// 배송 주소 및 필수 입력값 체크
    	if (odto.getGoods() == null || odto.getCnt() <= 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "상품 정보와 수량을 확인해주세요.");
        }
        // address: Daum 주소 검색 결과
        if (odto.getAddress() == null || odto.getAddress().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "배송 받을 주소를 입력하셔야 합니다.");
        }
        
        // detailAddress: 사용자가 직접 입력한 상세 주소
        if (odto.getDetailAddress() == null || odto.getDetailAddress().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "배송 받을 상세 주소를 입력하셔야 합니다.");
        }

        // 수령인 정보 검증 (receiverName, receiverPhone)
        if (odto.getReceiverName() == null || odto.getReceiverName().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "받는 사람 이름을 입력해주세요.");
        }
        
        if (odto.getReceiverPhone() == null || odto.getReceiverPhone().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "연락처를 입력해주세요.");
        }
        // 서비스에서 주문 DB 저장(READY 상태) 및 카카오페이 준비 호출
    	Map<String, Object> result = goodsOrdersService.readyPayment(odto, memberDto);
        return AjaxResponse.success(result);
    }

    @ResponseBody
    @PostMapping("/approve")
    public AjaxResponse approvePay(@RequestParam("pg_token") String pgToken, @RequestParam("tid") String tid) {
    	MemberDto memberDto = Common.idCheck(session);
    	Map<String, Object> result = goodsOrdersService.approvePayment(pgToken, tid, memberDto);
    	return AjaxResponse.success(result);
    }
    
    @ResponseBody
    @PostMapping("/failCancel")
    public AjaxResponse handleFailOrCancel(@RequestParam(name = "status", required = false, defaultValue = "실패") String status,
    		@RequestParam(name = "tid", required = false) String tid) {
        goodsOrdersService.failOrCancelPayment(tid, status);
        return AjaxResponse.success();
    }

    /**
     * 나의 주문 내역 조회 (페이징)
     */
    @ResponseBody
    @GetMapping("/getMyOrderList")
    public AjaxResponse getMyOrderList(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="size", defaultValue="10") int size) {
        
        MemberDto memberDto = Common.idCheck(session);
        Map<String, Object> map = goodsOrdersService.findMyOrders(memberDto, page, size);
        
        return AjaxResponse.success(map);
    }

    /**
     * 주문 상세 조회
     */
    @ResponseBody
    @GetMapping("/detail")
    public AjaxResponse detail(@RequestParam(name = "gono", required = false) Long gono) {
        MemberDto memberDto = Common.idCheck(session);
        GoodsOrdersDto order = goodsOrdersService.findByGonoSaler(gono, memberDto);
        return AjaxResponse.success(order);
    }
    
    /**
     * 결제 완료된 주문을 즉시 취소 (배송 전)
     */
    @ResponseBody
    @PostMapping("/cancel")
    public AjaxResponse cancelOrder(@RequestParam(name = "gono", required = false) Long gono) {
        MemberDto memberDto = Common.idCheck(session);
        Map<String, Object> result = goodsOrdersService.cancelOrder(gono, memberDto);
        return AjaxResponse.success(result);
    }
    
    /**
     * 판매자용: 주문 확인 (배송대기 -> 배송준비중)
     */
    @ResponseBody
    @PostMapping("/prepare")
    public AjaxResponse prepareOrder(@RequestParam(name = "gono", required = false) Long gono) {
        MemberDto seller = Common.idCheck(session);
        goodsOrdersService.prepareOrder(gono, seller);
        return AjaxResponse.success();
    }

    /**
     * 판매자용: 배송 시작 및 송장 등록 (배송준비중 -> 배송중)
     */
    @ResponseBody
    @PostMapping("/startShipping")
    public AjaxResponse startShipping(
    		@RequestParam(name = "gono", required = false) Long gono,
            @RequestBody Map<String, String> param) {
        
        MemberDto seller = Common.idCheck(session);
        String trackingNo = param.get("trackingNo");
        String gdelType = param.get("gdelType");

        if (trackingNo == null || trackingNo.trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "운송장 번호는 필수입니다.");
        }

        goodsOrdersService.startShipping(gono, trackingNo, gdelType, seller);
        return AjaxResponse.success();
    }
    
    /**
     * 판매자: 배송 상태 변경 및 송장 등록
     * 리액트의 updateDelivStatusApi와 매핑
     */
    @ResponseBody
    @PostMapping("/updateStatus")
    public AjaxResponse updateStatus(@RequestBody GoodsOrdersDto odto) {
        MemberDto seller = Common.idCheck(session);
        // odto에는 gono, delivStatus, trackingNo 등이 담겨서 옴
        goodsOrdersService.updateDeliveryStatus(odto, seller);
        return AjaxResponse.success();
    }

    /**
     * 판매자/관리자: 주문 강제 취소 (품절 등)
     * 리액트의 cancelOrderApi와 매핑
     */
    @ResponseBody
    @PostMapping("/adminCancel")
    public AjaxResponse adminCancel(@RequestBody Map<String, Object> params) {
        MemberDto seller = Common.idCheck(session);
        Long gono = Long.parseLong(params.get("gono").toString());
        String reason = (String) params.get("reason");
        
        goodsOrdersService.adminCancelOrder(gono, reason, seller);
        return AjaxResponse.success();
    }
}