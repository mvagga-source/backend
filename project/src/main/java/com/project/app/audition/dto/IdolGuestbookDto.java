package com.project.app.audition.dto;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "idol_guestbook")
public class IdolGuestbookDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 아이돌 프로필에 남긴 글인지 (IdolProfileDto의 profileId 참조)
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    // 누가 썼는지 (MemberDto의 id 혹은 닉네임)
    @Column(nullable = false)
    private String writer;

    // 응원 메시지 내용
    @Column(nullable = false, length = 1000)
    private String content;

    @CreationTimestamp
    @Column(updatable = false, name = "create_at")
    private LocalDateTime createAt;
}