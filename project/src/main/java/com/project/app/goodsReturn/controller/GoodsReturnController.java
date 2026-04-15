package com.project.app.goodsReturn.controller;

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
import com.project.app.goodsReturn.dto.GoodsReturnDto;
import com.project.app.goodsReturn.service.GoodsReturnService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/goodsReturn")
public class GoodsReturnController {

    @Autowired
    GoodsReturnService goodsReturnService;

    @Autowired
    HttpSession session;
    
    @ResponseBody
    @GetMapping("/list")
    public AjaxResponse getReturnList(@RequestParam Map<String, Object> param) {
        if(param.get("page") != null) {
            param.put("page", Integer.parseInt(param.get("page").toString()));
        }
        if(param.get("size") != null) {
            param.put("size", Integer.parseInt(param.get("size").toString()));
        }
        
        Map<String, Object> result = goodsReturnService.list(param);
        return AjaxResponse.success(result);
    }
    
    @ResponseBody
    @GetMapping("/SellerList")
    public AjaxResponse getSellerReturnList(@RequestParam Map<String, Object> param) {
    	if(param.get("page") != null) {
    		param.put("page", Integer.parseInt(param.get("page").toString()));
    	}
    	if(param.get("size") != null) {
    		param.put("size", Integer.parseInt(param.get("size").toString()));
    	}
    	
    	Map<String, Object> result = goodsReturnService.findSellerReturnList(param);
    	return AjaxResponse.success(result);
    }
    
    @ResponseBody
    @GetMapping("/detail")
    public AjaxResponse getReturnList(@RequestParam(name = "gono", required = false) Long gono) {
        return AjaxResponse.success(goodsReturnService.findByGono(gono, Common.idCheck(session)));
    }
    
    @ResponseBody
    @GetMapping("/detailSeller")
    public AjaxResponse detailSeller(@RequestParam(name = "rno", required = false) Long rno) {
        return AjaxResponse.success(goodsReturnService.findById(rno, Common.idCheck(session)));
    }
    
    /**
     * 반품 신청 접수 (배송 후)
     */
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(GoodsReturnDto returnRequest) {
        MemberDto memberDto = Common.idCheck(session);
        // 반품인지 교환인지 체크
        if (returnRequest.getReturnType() == null || returnRequest.getReturnType().trim().isEmpty()) {
        	throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품인지 교환인지 선택해주세요.");
        }
        // 반품 사유 체크
        if (returnRequest.getReturnReason() == null || returnRequest.getReturnReason().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품/교환 사유를 선택해주세요.");
        }
        // 상세 사유
        if (returnRequest.getReturnReasonDetail() == null || returnRequest.getReturnReasonDetail().trim().isEmpty()) {
        	throw new BaCdException(ErrorCode.INPUT_EMPTY, "상세 사유를 입력해주세요.");
        }
        // 반품 수량 체크
        if (returnRequest.getReturnCnt() <= 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품 수량은 1개 이상이어야 합니다.");
        }
        // 수거인 성함 체크
        if (returnRequest.getPickupName() == null || returnRequest.getPickupName().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "수거인 성함을 입력해주세요.");
        }
        // 수거 연락처 체크
        if (returnRequest.getPickupPhone() == null || returnRequest.getPickupPhone().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "수거인 전화번호를 입력해주세요.");
        }
        // 수거지 주소 체크
        if (returnRequest.getPickupAddr() == null || returnRequest.getPickupAddr().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "수거지 주소를 입력해주세요.");
        }
        // 수거지 상세주소 체크
        if (returnRequest.getPickupAddrDetail() == null || returnRequest.getPickupAddrDetail().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "수거지 상세주소를 입력해주세요.");
        }

        GoodsReturnDto result = goodsReturnService.requestReturn(returnRequest, memberDto);
        return AjaxResponse.success(result);
    }
    
    /**
     * 판매자: 반품/교환 상태 변경 (회수중, 완료, 거부 등)
     */
    @ResponseBody
    @PostMapping("/updateStatus")
    public AjaxResponse updateStatus(@RequestBody Map<String, Object> param) {
        // 필수 파라미터 체크 (rno, returnStatus)
        if (param.get("rno") == null) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품 고유 번호를 입력해주세요.");
        }else if(param.get("returnStatus") == null) {
        	throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품상태를 입력해주세요.");
        }
        
        Long rno = Long.parseLong(param.get("rno").toString());
        String nextStatus = param.get("returnStatus").toString();
        
        // 배송비, 택배사, 사유 등 추가 정보 추출
        Long gdelPrice = param.get("gdelPrice") != null ? Long.parseLong(param.get("gdelPrice").toString()) : null;
        String gdelType = (String) param.get("gdelType");
        String returnReasonDetail = (String) param.get("returnReasonDetail");

        // 서비스 호출
        GoodsReturnDto result = goodsReturnService.updateStatus(rno, nextStatus, gdelPrice, gdelType, returnReasonDetail);
        
        return AjaxResponse.success(result);
    }
    
    @ResponseBody
    @PostMapping("/delete")
    public AjaxResponse cancelReturn(@RequestParam(name="rno", required = false) Long rno) {
    	MemberDto memberDto = Common.idCheck(session);
        // delYn = 'y' 처리 로직 호출
        return AjaxResponse.success(goodsReturnService.delete(rno));
    }
}