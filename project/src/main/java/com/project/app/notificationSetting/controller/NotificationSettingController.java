package com.project.app.notificationSetting.controller;

import com.project.app.common.AjaxResponse;
import com.project.app.notificationSetting.dto.NotificationSettingDto;
import com.project.app.notificationSetting.service.NotificationSettingService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notification/setting")
@RequiredArgsConstructor
public class NotificationSettingController {

    private final NotificationSettingService settingService;

    /**
     * [설정 정보 조회]
     * 알림창의 설정 아이콘을 눌렀을 때 현재 사용자의 온/오프 상태를 가져옴
     */
    @GetMapping("/{memberId}")
    public AjaxResponse getSetting(@PathVariable("memberId") String memberId) {
        NotificationSettingDto setting = settingService.getSetting(memberId);
        return AjaxResponse.success(setting);
    }

    /**
     * [설정 정보 수정]
     * 체크박스나 토글 스위치를 변경했을 때 즉시 반영
     * body 예시: { "allowBoardLike": "n" }
     */
    @PostMapping("/{memberId}")
    public AjaxResponse updateSetting(
            @PathVariable("memberId") String memberId,
            @RequestBody Map<String, Object> updates) {
        NotificationSettingDto updated = settingService.updateSetting(memberId, updates);
        return AjaxResponse.success(updated).message("설정이 변경되었습니다.");
    }
}