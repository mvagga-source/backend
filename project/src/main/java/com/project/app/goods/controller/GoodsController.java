package com.project.app.goods.controller;

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

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired GoodsService goodsService;
    @Autowired HttpSession session;
    
    @Value("${img.host.url}")
    private String imgHostUrl;

    // 상품 목록 조회 (페이징 + 검색)
    @ResponseBody
    @GetMapping("/list")
    public AjaxResponse glist(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="size", defaultValue="12") int size,
            @RequestParam(name="category", required=false, defaultValue="") String category,
            @RequestParam(name="search", defaultValue="") String search,
            @RequestParam(name="minPrice", defaultValue="0") int minPrice,
            @RequestParam(name="maxPrice", defaultValue="0") int maxPrice,
            @RequestParam(name="sortDir", defaultValue="DESC") String sortDir) {
        Map<String, Object> map = goodsService.findAll(page, size, minPrice, maxPrice, category, search, "DESC");
        return AjaxResponse.success(map);
    }

    // 상품 상세 조회
    @ResponseBody
    @GetMapping("/view")
    public AjaxResponse view(@RequestParam(name="gno") Long gno) {
        GoodsDto goods = goodsService.findById(gno);
        return AjaxResponse.success(goods);
    }

    // 상품 등록 (이미지 업로드 포함)
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(GoodsDto gdto, 
    						@RequestParam(value="gimgFile", required=false) MultipartFile gimgFile) {
        MemberDto memberDto = Common.idCheck(session);
        
        if(gdto.getGname() == null || gdto.getGname().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "상품명을 입력해주세요.");
        }
        else if(gdto.getPrice() == null || gdto.getPrice() <= 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "가격을 입력해주세요.");
        }

        // 파일 업로드 처리 (게시판 로직 응용)
        if(gimgFile != null && !gimgFile.isEmpty()) {
            String fName = gimgFile.getOriginalFilename();
            String refName = System.currentTimeMillis() + "_" + fName;
            String uploadPath = "c:/upload/goods/"; // 굿즈 전용 경로
            
            try {
                File folder = new File(uploadPath);
                if(!folder.exists()) folder.mkdirs();
                gimgFile.transferTo(new File(uploadPath + refName));
                gdto.setGimg(imgHostUrl+"/goods/" + refName); // DTO에 파일명 저장
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        gdto.setMember(memberDto);
        goodsService.save(gdto);
        return AjaxResponse.success();
    }

    // 상품 삭제 (Soft Delete)
    @ResponseBody
    @PostMapping("/delete")
    public AjaxResponse delete(@RequestParam(name="gno") Long gno) {
        MemberDto memberDto = Common.idCheck(session);
        goodsService.delete(gno, memberDto);
        return AjaxResponse.success();
    }
}