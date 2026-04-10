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
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")


@RequestMapping("/api/idolProfile")
public class IdolProfileController {
	@Autowired
	private IdolProfileService idolProfileService;
	
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
	@GetMapping("/getIdolProfile") 
	public ResponseEntity<?> getIdolDetail(@RequestParam("idolProfileId") Long idolProfileId) {
	    System.out.println("👉 상세 정보 및 사진 요청 ID: " + idolProfileId);
	    
	    try {
	        // 기존의 findById 대신 사진 리스트가 포함된 getIdolDetail을 호출합니다!
	        Map<String, Object> result = idolProfileService.getIdolDetail(idolProfileId);
	        
	        if (result == null || result.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        // result 안에는 "profile"과 "mediaList"가 담겨서 리액트로 전달됩니다.
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("데이터 조회 중 서버 오류");
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
	
    
    // 하단 이미지
   
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailedProfile(@PathVariable(name = "id") Long id) { // 메서드명 변경으로 중복 해결
        try {
            // 변수명을 상단에 주입된 idolProfileService로 정확히 호출
            Map<String, Object> result = idolProfileService.getIdolDetail(id);
            
            if (result == null || result.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("데이터 조회 중 서버 오류");
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
