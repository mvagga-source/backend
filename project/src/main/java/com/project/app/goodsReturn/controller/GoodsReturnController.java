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
    	MemberDto memberDto = Common.idCheck(session);
        return AjaxResponse.success(goodsReturnService.findByGono(gono, memberDto));
    }
    
    @ResponseBody
    @GetMapping("/view")
    public AjaxResponse view(@RequestParam(name = "rno", required = false) Long rno) {
    	MemberDto memberDto = Common.idCheck(session);
        return AjaxResponse.success(goodsReturnService.view(rno, memberDto));
    }
    
    @ResponseBody
    @GetMapping("/detailSeller")
    public AjaxResponse detailSeller(@RequestParam(name = "rno", required = false) Long rno) {
    	MemberDto memberDto = Common.idCheck(session);
        return AjaxResponse.success(goodsReturnService.findById(rno, memberDto));
    }
    
    /**
     * 반품 신청 접수 (배송 후)
     */
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(GoodsReturnDto dto) {
        MemberDto memberDto = Common.idCheck(session);
        validateSaveAndUpdate(dto);
        // 반품 수량 체크
        if (dto.getReturnCnt() <= 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품 수량은 1개 이상이어야 합니다.");
        }

        GoodsReturnDto result = goodsReturnService.requestReturn(dto, memberDto);
        return AjaxResponse.success(result);
    }
    
    /**
     * 반품 수정
     */
    @ResponseBody
    @PostMapping("/update")
    public AjaxResponse update(GoodsReturnDto dto) {
    	MemberDto memberDto = Common.idCheck(session);
    	validateSaveAndUpdate(dto);
    	
    	GoodsReturnDto result = goodsReturnService.update(dto, memberDto);
    	return AjaxResponse.success(result);
    }
    
    /**
     * 판매자: 반품/교환 상태 변경 (회수중, 완료, 거부 등)
     */
    @ResponseBody
    @PostMapping("/updateStatus")
    public AjaxResponse updateStatus(@RequestBody Map<String, Object> param) {
    	MemberDto memberDto = Common.idCheck(session);
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
        String returnSaleReasonDetail = (String) param.get("returnSaleReasonDetail");

        // 서비스 호출
        GoodsReturnDto result = goodsReturnService.updateStatus(rno, nextStatus, gdelPrice, gdelType, returnSaleReasonDetail, memberDto);
        
        return AjaxResponse.success(result);
    }
    
    /*
     * 반품취소 
     */
    @ResponseBody
    @PostMapping("/delete")
    public AjaxResponse cancelReturn(@RequestParam(name="rno", required = false) Long rno) {
    	MemberDto memberDto = Common.idCheck(session);
    	//삭제 처리할지는 놔두기
        return AjaxResponse.success(goodsReturnService.delete(rno));
    }
    
    public void validateSaveAndUpdate(GoodsReturnDto dto) {
    	// 반품인지 교환인지 체크
        if (dto.getReturnType() == null || dto.getReturnType().trim().isEmpty()) {
        	throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품인지 교환인지 선택해주세요.");
        }
        // 반품 사유 체크
        if (dto.getReturnReason() == null || dto.getReturnReason().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "반품/교환 사유를 선택해주세요.");
        }
        // 상세 사유
        if (dto.getReturnReasonDetail() == null || dto.getReturnReasonDetail().trim().isEmpty()) {
        	throw new BaCdException(ErrorCode.INPUT_EMPTY, "상세 사유를 입력해주세요.");
        }
        // 수거인 성함 체크
        if (dto.getPickupName() == null || dto.getPickupName().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "수거인 성함을 입력해주세요.");
        }
        // 수거 연락처 체크
        if (dto.getPickupPhone() == null || dto.getPickupPhone().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "수거인 전화번호를 입력해주세요.");
        }
        // 수거지 주소 체크
        if (dto.getPickupAddr() == null || dto.getPickupAddr().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "수거지 주소를 입력해주세요.");
        }
        // 수거지 상세주소 체크
        if (dto.getPickupAddrDetail() == null || dto.getPickupAddrDetail().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "수거지 상세주소를 입력해주세요.");
        }
    }
}