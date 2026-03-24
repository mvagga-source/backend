package com.project.app.goodsReviewLike.controller;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goods.service.GoodsService;
import com.project.app.goodsReviewLike.service.GoodsReviewLikeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/goodsReviewLike")
public class GoodsReviewLikeController {

    @Autowired GoodsReviewLikeService goodsReviewLikeService;
    @Autowired HttpSession session;

    //도움돼요
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(@RequestParam(value="grno", required=false) Long grno) {
        MemberDto memberDto = Common.idCheck(session);
        return AjaxResponse.success(goodsReviewLikeService.save(grno, memberDto));
    }
}