package com.project.app.notification.dto;

import com.project.app.auth.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "notification")
public class NotificationDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notino;		//알림 pk

    // 수신자 (Member와 연관관계 설정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberDto member;		// 알림 수신자
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private MemberDto sender;		// 알림 송신자

    @Lob
    private String nocontent; // 알림 내용 (일괄 변경시 DB이용, 현재 코드값으로 사용하지 않는 상태)

    @Column(length = 100)
    private String type;    // 알림 종류 (토스트메세지, 채팅, 공지사항, VOTE, COMMUNITY, SYSTEM 등)

    @Lob
    private String url;     // 클릭 시 이동할 페이지 주소(사용될지는 모름)

    @ColumnDefault("'n'") // n: 읽지않음, y: 읽음
    @Column(name = "is_read", length = 1)
    private String isRead = "n"; // 읽음 여부 (기본값 n: 읽지않음, y: 읽음)

    @CreationTimestamp
    @Column(name = "crdt", updatable = false)
    private Timestamp crdt;		//등록일
    
    @UpdateTimestamp
    @Column(name = "updt")
    private Timestamp updt;		//수정일
}