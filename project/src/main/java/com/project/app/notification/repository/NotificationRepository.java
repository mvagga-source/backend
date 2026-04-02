package com.project.app.notification.repository;

import com.project.app.notification.dto.NotificationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationDto, Long> {

	// 특정 회원의 읽지 않은 알림 개수 조회 (isRead가 'n'인 것)
    public long countByMemberIdAndIsRead(String memberId, String isRead);

    // 무한 스크롤용: 특정 ID(notino)보다 작은 이전 알림 조회
    @Query("SELECT n FROM NotificationDto n WHERE n.member.id = :memberId AND n.notino < :lastId ORDER BY n.notino DESC")
    public List<NotificationDto> findNotificationsBefore(@Param("memberId") String memberId, @Param("lastId") Long lastId, Pageable pageable);

    // 첫 진입 시 최신 알림 조회
    public List<NotificationDto> findTop10ByMemberIdOrderByNotinoDesc(String memberId);

    public List<NotificationDto> findAllByMemberIdAndIsRead(String memberId, String string);

	public void deleteByMemberId(String memberId);
}