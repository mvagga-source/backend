package com.project.app.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.app.admin.service.AdminAuditionService;
import com.project.app.audition.dto.AuditionDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminAuditionService adminAuditionService;

    // ── AuditionDto → Map 변환 (JSP EL Boolean 오류 방지) ──
    private Map<String, Object> toMap(AuditionDto a) {
        Map<String, Object> map = new HashMap<>();
        map.put("auditionId",    a.getAuditionId());
        map.put("round",         a.getRound());
        map.put("title",         a.getTitle());
        map.put("startDate",     a.getStartDate());
        map.put("endDate",       a.getEndDate());
        map.put("maxVoteCount",  a.getMaxVoteCount());
        map.put("survivorCount", a.getSurvivorCount());
        map.put("hasTeamMatch",  Boolean.TRUE.equals(a.getHasTeamMatch()) ? "true" : "false");
        map.put("bonusRate",     a.getBonusRate());
        map.put("status",        a.getStatus());
        return map;
    }

    // ── 관리자 메인 페이지 렌더링 ────────────────────────
    // GET /admin/main
    @GetMapping("/main")
    public String adminMain(Model model) {
        List<Map<String, Object>> auditionList = adminAuditionService.getAuditionList()
            .stream()
            .map(this::toMap)
            .collect(Collectors.toList());
        model.addAttribute("auditionList", auditionList);
        return "admin/adminMain";
    }
}
