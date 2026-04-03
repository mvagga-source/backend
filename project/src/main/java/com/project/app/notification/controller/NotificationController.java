package com.project.app.notification.controller;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;
import com.project.app.notification.dto.NotificationDto;
import com.project.app.notification.service.NotificationService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
	
	//SSE적용시 문제
	//1. 데이터 중복 문제(무한스크롤과 더보기에서 일어나는 쌓일때 문제)
	//2. 로딩 중 알림 수신 (서버에서 받는 중 계속 알림이 쌓일 경우 누락 문제)
	//3. 하트비트 재연결시 스케쥴링 부하(미미하다고 하지만 주의)
	//브라우저는 동일한 도메인에 대해 SSE 연결 개수 제한(보통 6개) : 개발 중 새로고침을 반복하거나 창을 여러 개 띄우면 연결이 고갈되어 사이트 자체가 안 열릴 수 있음.

    private final NotificationService notificationService;
    
    @Autowired
    HttpSession session;
    
    /**
     * [알림 전송 및 저장]
     * 프론트엔드의 showToast(..., memberId) 호출 시 실행됨
     */
    @PostMapping("/send")
    public AjaxResponse sendNotification(NotificationDto notification) {
        try {
            return AjaxResponse.success(notificationService.createAndSend(notification)).message("알림이 성공적으로 전송되었습니다.");
        } catch (BaCdException e) {
            return AjaxResponse.success(false).message(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.success(false).message("알림 처리 중 오류가 발생했습니다.");
        }
    }
    
    @PostMapping("/readbulk")
    public AjaxResponse readBulkNotifications(@RequestBody List<Long> notinoList) {
    	notificationService.readBulkNotifications(notinoList);
        return AjaxResponse.success();
    }

    /**
     * [초기 데이터 조회] (안읽은 개수 + 최근 리스트 10개)
     * 사이드바를 열었을 때 안읽은 개수와 최신 10개를 가져옴
     */
    @GetMapping("/init/{memberId}")
    public AjaxResponse getInitialData(@PathVariable("memberId") String memberId) {
        Map<String, Object> data = notificationService.getInitialData(memberId);
        return AjaxResponse.success(data);
    }

    /**
     * [무한 스크롤 / 더보기] (더보기 - 특정 ID보다 작은 알림들)
     * @lastId: 현재 리스트의 가장 마지막(가장 오래된) 알림 ID
     */
    @GetMapping("/more/{memberId}")
    public AjaxResponse getMoreNotifications(
            @PathVariable("memberId") String memberId, 
            @RequestParam(name = "lastId", required = false) Long lastId) {
        return AjaxResponse.success(notificationService.getMoreNotifications(memberId, lastId));
    }
    
	/**
	 * [개별 삭제]
	 * @param notino
	 * @return
	 */
    @DeleteMapping("/delete/{notino}")
    public AjaxResponse deleteNotification(@PathVariable("notino") Long notino) {
        notificationService.deleteNotification(notino);
        return AjaxResponse.success();
    }

    /**
     * [전체 삭제]
     * @param memberId
     * @return
     */
    @DeleteMapping("/deleteall/{memberId}")
    public AjaxResponse deleteAllNotifications(@PathVariable("memberId") String memberId) {
        notificationService.deleteAllNotifications(memberId);
        return AjaxResponse.success();
    }
}