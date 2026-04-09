package com.project.app.audition.repository;

import com.project.app.audition.dto.IdolGuestbookDto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IdolGuestbookRepository extends JpaRepository<IdolGuestbookDto, Long> {
    // 특정 아이돌의 방명록만 최신순으로 가져오기
	List<IdolGuestbookDto> findByProfileIdOrderByIdDesc(Long profileId);
}