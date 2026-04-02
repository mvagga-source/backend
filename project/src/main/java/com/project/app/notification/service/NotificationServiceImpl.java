package com.project.app.notification.service;

import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.service.MemberService;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.notification.dto.NotificationDto;
import com.project.app.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    
    private final MemberService memberService;
    
	// 유저별 SSE 연결 관리
    //private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /*@Override
    public SseEmitter subscribe(String memberId) throws BaCdException {
        // 기존 코드와 동일하게 유지하되 재연결 시 기존 emitter 제거 로직 권장
        if(emitters.containsKey(memberId)) emitters.remove(memberId);
        
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);
        emitters.put(memberId, emitter);
        
        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            emitters.remove(memberId);
        }
        return emitter;
    }

    @Override
    @Transactional
    public void sendNotification(NotificationDto notification) throws BaCdException {
        notificationRepository.save(notification);
        String memberId = notification.getMember().getId();

        if (emitters.containsKey(memberId)) {
            SseEmitter emitter = emitters.get(memberId);
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }*/
    
    @Override
    @Transactional
    public NotificationDto createAndSend(MemberDto member, String senderId, String nocontent, String type, String url) throws BaCdException {
        // 1. DTO (또는 Entity) 생성
        NotificationDto notification = new NotificationDto();
        MemberDto sender = memberService.findById(member);
        notification.setMember(member);
        notification.setSender(sender);
        notification.setNocontent(nocontent);
        notification.setType(type);
        notification.setUrl(url);
        notification.setIsRead("n");

        // 2. 기존에 만들어둔 저장 및 전송 로직 재활용
        //this.sendNotification(notification);
        return notificationRepository.save(notification);
    }

    // 초기 데이터 조회 (리스트 10개 + 안읽은 개수)
    public Map<String, Object> getInitialData(String memberId) throws BaCdException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("list", notificationRepository.findTop10ByMemberIdOrderByNotinoDesc(memberId));
        result.put("unreadCount", notificationRepository.countByMemberIdAndIsRead(memberId, "n"));
        return result;
    }

    // 무한 스크롤 조회
    public List<NotificationDto> getMoreNotifications(String memberId, Long lastId) throws BaCdException {
        Pageable pageable = PageRequest.of(0, 10);
        return notificationRepository.findNotificationsBefore(memberId, lastId, pageable);
    }
    
    //읽음처리
    @Transactional
    public void readBulkNotifications(List<Long> notinoList) throws BaCdException {
        List<NotificationDto> targetList = notificationRepository.findAllById(notinoList);
        for (NotificationDto noti : targetList) {
            if ("n".equals(noti.getIsRead())) {
                noti.setIsRead("y");
            }
        }
    }

    // 읽음 처리
    @Transactional
    public NotificationDto readNotification(Long notino) throws BaCdException {
        NotificationDto noti = notificationRepository.findById(notino).orElseThrow(() -> new BaCdException(ErrorCode.NOTIFICATION_NOT_FOUND));
        noti.setIsRead("y");
        return noti;
    }
    
    @Override
    @Transactional
    public List<NotificationDto> readAllNotifications(String memberId) throws BaCdException {
        // 해당 사용자의 모든 'n' 상태인 알림 조회
        List<NotificationDto> unreadList = notificationRepository.findAllByMemberIdAndIsRead(memberId, "n");
        
        // 일괄 업데이트 (Dirty Checking 활용)
        for (NotificationDto noti : unreadList) {
            noti.setIsRead("y");
        }
        
        return unreadList;
    }
    
    //SSE 끊긴 후 재연결		// 30초마다 실행(호출하지 않아도 자동 스케쥴링 실행)
    /*@Scheduled(fixedDelay = 30000) // 30초마다 실행
    public void sendHeartbeat() throws BaCdException {		//단일 서버라 문제 없지만 이후 서버 2개로 늘리거나 할 경우 적용안됨 redis 활용하거나 기타방식으로 적용 등
    	if (emitters.isEmpty()) return; // 연결된 유저가 없으면 즉시 종료 (부하 방지)
    	
    	//유저가 연결을 끊거나 타임아웃되면 onCompletion, onTimeout 콜백을 통해 맵에서 제거
        emitters.forEach((memberId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("ping").data(""));
            } catch (Exception e) {
            	// 실패 시 확실히 제거
                emitters.remove(memberId);
            }
        });
    }*/
}