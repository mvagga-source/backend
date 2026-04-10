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
    @GetMapping("/detail")
    public AjaxResponse getReturnList(@RequestParam(name = "gono", required = false) Long gono) {
        return AjaxResponse.success(goodsReturnService.findByGono(gono, Common.idCheck(session)));
    }
    
    /**
     * 반품 신청 접수 (배송 후)
     */
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(GoodsReturnDto returnRequest) {
        MemberDto memberDto = Common.idCheck(session);
        GoodsReturnDto result = goodsReturnService.requestReturn(returnRequest, memberDto);
        return AjaxResponse.success(result);
    }
    
    @ResponseBody
    @PostMapping("/delete")
    public AjaxResponse cancelReturn(@RequestParam(name="rno", required = false) Long rno) {
        // delYn = 'y' 처리 로직 호출
        return AjaxResponse.success(goodsReturnService.delete(rno));
    }
}