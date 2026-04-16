package com.project.app.audition.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.SupportMember;


// JpaRepository<엔티티클래스, ID타입> 이어야 합니다.
public interface SupportMemberRepository extends JpaRepository<SupportMember, Long> {
    
    // 이미지의 컬럼명 SUPPORT_ID와 매핑된 엔티티 필드명을 사용해야 합니다.
    // 엔티티 내에 SupportProject 객체가 있고 그 안에 supportId가 있다면 아래 메서드가 작동합니다.
    List<SupportMember> findBySupportProject_SupportIdOrderByCreatedAtDesc(Long supportId);
    
    List<SupportMember> findTop10BySupportProjectSupportIdOrderByCreatedAtDesc(Long supportId);
}