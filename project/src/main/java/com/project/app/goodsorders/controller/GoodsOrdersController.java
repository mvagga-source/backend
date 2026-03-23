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
@RequestMapping("/api/goodsorders")
public class GoodsOrdersController {

    @Autowired
    GoodsOrdersService ordersService;

    @Autowired
    HttpSession session;

    /**
     * 주문 생성 (결제 준비 단계)
     */
    @ResponseBody
    @PostMapping("/create")
    public AjaxResponse createOrder(@RequestBody GoodsOrdersDto odto) {
        MemberDto memberDto = Common.idCheck(session); // 로그인 체크
        
        if (odto.getGoods() == null || odto.getCnt() <= 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "상품 정보와 수량을 확인해주세요.");
        }

        odto.setMember(memberDto);
        odto.setStatus("READY"); // 초기 상태는 대기
        
        // 주문 고유번호 생성 (예: ORD-UUID)
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        odto.setOrderId(orderId);

        GoodsOrdersDto savedOrder = ordersService.createOrder(odto);
        return AjaxResponse.success(savedOrder);
    }

    /**
     * 나의 주문 내역 조회 (페이징)
     */
    @ResponseBody
    @GetMapping("/myList")
    public AjaxResponse myList(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="size", defaultValue="10") int size) {
        
        MemberDto memberDto = Common.idCheck(session);
        Map<String, Object> map = ordersService.findMyOrders(memberDto, page, size);
        
        return AjaxResponse.success(map);
    }

    /**
     * 주문 상세 조회
     */
    @ResponseBody
    @GetMapping("/detail/{gono}")
    public AjaxResponse detail(@PathVariable("gono") Long gono) {
        MemberDto memberDto = Common.idCheck(session);
        GoodsOrdersDto order = ordersService.findByGono(gono);
        
        // 본인 주문인지 확인
        if(!order.getMember().getId().equals(memberDto.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);
        }
        
        return AjaxResponse.success(order);
    }
}