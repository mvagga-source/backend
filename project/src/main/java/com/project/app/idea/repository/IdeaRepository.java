package com.project.app.idea.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.app.idea.dto.IdeaDto;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<IdeaDto, Long> {
    
	/**
     * "더보기" 기능을 위해 Slice를 사용합니다.
     * Slice는 전체 카운트 쿼리를 날리지 않고 다음 페이지 존재 여부(hasNext)만 확인하여 성능상 유리합니다.
     */
    Slice<IdeaDto> findAllByOrderByCrdtDesc(Pageable pageable);
    
    // 특정 회원이 작성한 아이디어 목록 (최신순)
    List<IdeaDto> findByMember_IdOrderByCrdtDesc(String memberId);
}
