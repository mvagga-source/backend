package com.project.app.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Model 추가
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.audition.dto.IdolMediaDto;
import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.service.IdolProfileService;

@Controller
public class AdminViewController {

    @Autowired
    private IdolProfileService idolProfileService; // 서비스 주입

    /**
     * 목록 조회: 데이터를 Model에 담아야 JSP에서 보입니다.
     */
    @GetMapping("/admin/profile/list")
    public String adminProfileList(Model model, 
                                 @RequestParam(value = "page", defaultValue = "1") int page) {
        
        // 중요: JSP에서 ${profileList.list} 로 접근하므로 이름을 맞춰서 보냅니다.
        // 서비스에 해당 메서드가 없다면 아래 2번 항목을 참고해서 만드세요.
        Map<String, Object> profileList = idolProfileService.getProfileListForAdmin(page);
        System.out.println("👉 profileList 데이터 확인: " + profileList);
        if (profileList == null) {
            profileList = new HashMap<>();
            profileList.put("list", new ArrayList<>());
            profileList.put("totalCount", 0);
        }
        
        System.out.println("👉 Controller 최종 바인딩 데이터: " + profileList);
        model.addAttribute("profileList", profileList); 
        
        return "admin/profile/list"; 
    }
    
    /**
     * 저장 로직
     */
    @PostMapping("/admin/idol/save") // JSP 폼 액션과 일치시킴
    public String saveIdolProfile(
        @ModelAttribute IdolProfileDto profileDto,
        @RequestParam("mainImgFile") MultipartFile mainImgFile,
        @RequestParam(value = "subImgFiles", required = false) List<MultipartFile> subImgFiles 
    ) {
        // 인스턴스(idolProfileService)를 통해 메서드 호출
        if (mainImgFile != null && !mainImgFile.isEmpty()) {
            String mainUrl = idolProfileService.upload(mainImgFile);
            profileDto.setMainImgUrl(mainUrl);
        }

        // 프로필 정보 저장
        IdolProfileDto savedProfile = idolProfileService.save(profileDto);

        // 하단 갤러리 이미지들 저장
        if (subImgFiles != null) {
            for (MultipartFile file : subImgFiles) {
                if (!file.isEmpty()) {
                    String subUrl = idolProfileService.upload(file);
                    IdolMediaDto media = IdolMediaDto.builder()
                        .profile(savedProfile)
                        .type("PHOTO")
                        .url(subUrl)
                        .description("컨셉 포토")
                        .build();
                    idolProfileService.saveMedia(media); // 미디어 저장 전용 메서드 호출 권장
                }
            }
        }
        return "redirect:/admin/profile/list";
    }
}