package com.project.app.idea.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.app.idea.dto.IdeaDto;
import com.project.app.report.dto.ReportDto;

import java.util.List;

public interface IdeaRepository extends JpaRepository<IdeaDto, Long> {
    
	/**
     * "더보기" 기능을 위해 Slice를 사용합니다.
     * Slice는 전체 카운트 쿼리를 날리지 않고 다음 페이지 존재 여부(hasNext)만 확인하여 성능상 유리합니다.
     */
	// 전체 신고 내역 조회 (lastId보다 작은 것 10개)
    @Query("SELECT r FROM IdeaDto r WHERE r.ideano < :lastId ORDER BY r.ideano DESC")
    Slice<IdeaDto> findNextPageAll(@Param("lastId") Long lastId, Pageable pageable);

    // 전체 신고 개수
    long count();
    
    // 특정 회원이 작성한 아이디어 목록 (최신순)
    List<IdeaDto> findByMember_IdOrderByCrdtDesc(String memberId);
}
