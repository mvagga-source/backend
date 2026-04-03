package com.project.app.notification.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.exception.BaCdException;
import com.project.app.notification.dto.NotificationDto;

public interface NotificationService {
	
	public NotificationDto createAndSend(NotificationDto notification) throws BaCdException;
	
	public Map<String, Object> getInitialData(String memberId) throws BaCdException;
	
	public List<NotificationDto> getMoreNotifications(String memberId, Long lastId);
	
	public void readBulkNotifications(List<Long> notinoList) throws BaCdException;
	
	public NotificationDto readNotification(Long notino) throws BaCdException;
	
	public List<NotificationDto> readAllNotifications(String memberId) throws BaCdException;
	
	public void deleteNotification(Long notino) throws BaCdException;
	
	public void deleteAllNotifications(String memberId) throws BaCdException;
}
