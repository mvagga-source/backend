package com.project.app.audition.controller;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.service.IdolProfileService;
import com.project.app.common.AjaxResponse;
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

@RequestMapping("/api/idolProfile")
public class IdolProfileController {
	@Autowired
	IdolProfileService idolProfileService;
	
	@ResponseBody
	@GetMapping("/idolViewVote")
	public Map<String, Object> idolprofileVote(@RequestParam(name="auditionId", required = false) Long auditionId, 
			@RequestParam(name="idolProfileId", required = false) Long idolProfileId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("idolProfile", idolProfileService.findIdolWithVote(auditionId, idolProfileId));
		return map;
	}
	
 // ── 개인 프로필 ────────────────────────────────────
 // GET /api/audition/profile?auditionId=2&idolProfileId=1
//    @GetMapping("/getIdolProfile")
//    public IdolProfileDto getIdolDetail( @RequestParam("idolProfileId") Long idolProfileId) {
//
//    IdolProfileDto idolProfileDto = idolProfileService.findById(idolProfileId);	
//    	return idolProfileDto;
//    }
	@GetMapping("/getIdolProfile") // 경로를 리액트가 찾는 주소로 원복합니다.
    public ResponseEntity<IdolProfileDto> getIdolDetail(@RequestParam("idolProfileId") Long idolProfileId) {
        
        System.out.println("👉 요청 들어온 ID: " + idolProfileId);
        
        try {
            IdolProfileDto idolProfileDto = idolProfileService.findById(idolProfileId);
            
            if (idolProfileDto == null) {
                System.out.println("❌ DB에 데이터 없음");
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(idolProfileDto);
        } catch (Exception e) {
            e.printStackTrace(); // 서버 콘솔에 진짜 에러 원인을 찍어줍니다.
            return ResponseEntity.status(500).build();
        }
    }
	
    
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(
        @RequestParam("image") MultipartFile file,
        @RequestParam("idolProfileId") Long idolProfileId) {

        String uploadDir = "C:/upload/";
        
        // 1. UUID로 이름 변경 (한글 파일명 지랄 방지)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;

        try {
            File dest = new File(uploadDir + newFilename);
            file.transferTo(dest);
            
            idolProfileService.updateMainImgUrl(idolProfileId, newFilename);
            
            // ✅ 예외 처리가 포함된 슬립
            Thread.sleep(500); 
            
            return ResponseEntity.ok().body(newFilename);
        } catch (IOException | InterruptedException e) { // InterruptedException 추가
            e.printStackTrace();
            return ResponseEntity.status(500).body("저장 실패");
        }
    }	
	
    /**
     * 아이돌 selectBox
     */
    @ResponseBody
   	@GetMapping("/idolSelectBox")
   	public AjaxResponse idolSelectBoxList() {
   		return AjaxResponse.success(idolProfileService.findAll());
   	}
}
