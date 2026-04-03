package com.project.app.notification.event;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.exception.BaCdException;
import com.project.app.notification.dto.NotificationDto;
import com.project.app.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    @Async // 비동기로 동작 (메인 스레드와 별도의 스레드에서 진행, 메인 로직 속도 저하 방지)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationDto event) throws BaCdException {
    	notificationService.createAndSend(event);
    }
}