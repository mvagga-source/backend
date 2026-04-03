package com.project.app.audition.controller;

import com.project.app.audition.dto.SupportMember;
import com.project.app.audition.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    // 1. 후원하기 실행 (POST)
    @PostMapping("/donate")
    public ResponseEntity<?> donate(@RequestBody Map<String, Object> payload) {
        Long supportId = Long.valueOf(payload.get("supportId").toString());
        String nickname = (String) payload.get("nickname");
        Long amount = Long.valueOf(payload.get("amount").toString());

        supportService.saveSupport(supportId, nickname, amount);
        return ResponseEntity.ok("후원이 완료되었습니다.");
    }

    // 2. 실시간 로그 가져오기 (GET)
    // @PathVariable에 ("supportId") 이름을 명시하여 파라미터 인식 문제를 해결합니다.
    @GetMapping("/logs/{supportId}")
    public ResponseEntity<List<SupportMember>> getLogs(@PathVariable("supportId") Long supportId) {
        List<SupportMember> logs = supportService.getLogsByProjectId(supportId);
        return ResponseEntity.ok(logs);}
        
        
 // 3. 서포트 프로젝트 상세 정보 가져오기 (GET)
    @GetMapping("/project/{supportId}")
    public ResponseEntity<?> getSupportProject(@PathVariable("supportId") Long supportId) {
        // 이 부분은 서비스에 해당 로직이 구현되어 있어야 합니다.
        // 예시: supportService.getProjectById(supportId)
        var project = supportService.getProjectById(supportId); 
        return ResponseEntity.ok(project);
    
    }
}