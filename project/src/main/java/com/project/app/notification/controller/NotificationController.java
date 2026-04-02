package com.project.app.notification.controller;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;
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
     * [SSE 구독] (이건 text/event-stream이라 AjaxResponse를 사용하지 않음)
     * @LastEventID: 클라이언트가 마지막으로 수신한 알림 ID (재연결 시 누락 데이터 방지)
     */
    /*@GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("memberId") String memberId) {
    	try {
    		return notificationService.subscribe(memberId);
    	} catch (Exception e) {
            return null; 
        }
    }*/
    
    /**
     * [알림 전송 및 저장]
     * 프론트엔드의 showToast(..., memberId) 호출 시 실행됨
     */
    @PostMapping("/send")
    public AjaxResponse sendNotification(@RequestBody Map<String, Object> params) {
        try {
            // 프론트에서 보낸 데이터 추출
        	MemberDto member = Common.idCheck(session);
			String senderId = (String) params.get("senderId");
            String nocontent = (String) params.get("nocontent");
            String type = (String) params.get("type");
            String url = (String) params.get("url");
            return AjaxResponse.success(notificationService.createAndSend(member, senderId, nocontent, type, url)).message("알림이 성공적으로 전송되었습니다.");
        } catch (BaCdException e) {
            return AjaxResponse.success(false).message(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.success(false).message("알림 처리 중 오류가 발생했습니다.");
        }
    }
    
    @PostMapping("/read-bulk")
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
     * [단건 읽음 처리] 알림 읽음 처리
     */
    /*@PatchMapping("/read/{notino}")
    public AjaxResponse readNotification(@PathVariable Long notino) {
        notificationService.readNotification(notino);
        return AjaxResponse.success().message("읽음 처리 완료");
    }*/
    
    /**
     * [전체 읽음 처리]
     * 사용자가 일일이 누르기 힘들 때를 대비한 기능
     */
    /*@PatchMapping("/read-all/{memberId}")
    public AjaxResponse readAllNotifications(@PathVariable("memberId") String memberId) {
        try {
            notificationService.readAllNotifications(memberId);
            return AjaxResponse.success().message("모든 알림을 읽음 처리했습니다.");
        } catch (Exception e) {
            return AjaxResponse.success(false).message("처리에 실패했습니다.");
        }
    }*/
}