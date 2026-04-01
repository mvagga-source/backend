package com.project.app.audition.repository;

import com.project.app.audition.dto.IdolMediaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IdolMediaRepository extends JpaRepository<IdolMediaDto, Long> {
    // profile_id로 해당 아이돌의 모든 미디어(사진/영상)를 찾아오는 메서드
    List<IdolMediaDto> findByProfileProfileId(Long profileId);
}