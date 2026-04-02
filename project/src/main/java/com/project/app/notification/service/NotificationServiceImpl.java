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
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    
    private final SimpMessagingTemplate messagingTemplate;
    
    @Override
    @Transactional
    public NotificationDto createAndSend(MemberDto member, String senderId, String nocontent, String type, String url) throws BaCdException {
        // 1. DTO (또는 Entity) 생성
        MemberDto sender = memberService.findById(member);
        NotificationDto notification = NotificationDto.builder()
                .member(member)
                .sender(sender)
                .nocontent(nocontent)
                .type(type)
                .url(url)
                .isRead("n")
                .build();

        // 2. 기존에 만들어둔 저장 및 전송 로직 재활용
        //this.sendNotification(notification);
        NotificationDto savedNoti = notificationRepository.save(notification);
        // 유저는 본인의 ID가 포함된 경로(/sub/notification/{userId})를 구독하고 있음
        messagingTemplate.convertAndSend("/sub/notification/" + member.getId(), savedNoti);
        return savedNoti;
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
    
    /**
     * [개별 삭제] 
     * 특정 알림 번호(notino)를 기준으로 DB에서 즉시 삭제
     */
    @Transactional
    public void deleteNotification(Long notino) throws BaCdException {
        // 존재 여부 확인 후 삭제 (에러 방지)
        if (notificationRepository.existsById(notino)) {
            notificationRepository.deleteById(notino);
        }
    }

    /**
     * [전체 삭제] 
     * 특정 사용자의 모든 알림 데이터를 DB에서 일괄 삭제
     */
    @Transactional
    public void deleteAllNotifications(String memberId) throws BaCdException {
        // 해당 사용자의 알림만 찾아서 삭제하는 쿼리 메소드 호출
        notificationRepository.deleteByMemberId(memberId);
    }
}