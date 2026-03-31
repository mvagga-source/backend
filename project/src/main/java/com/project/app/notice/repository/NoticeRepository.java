package com.project.app.notice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.notice.dto.NoticeDto;

public interface NoticeRepository extends JpaRepository<NoticeDto, Long> {

    /**
     * 1. 메인 화면용: 최신 공지사항 5개 추출
     * 조건: 삭제되지 않음(delYn='n') AND 현재 시간이 시작~종료 사이임
     * 정렬: 중요공지(isPinned) 우선 -> 최신순(nno DESC)
     */
    List<NoticeDto> findTop5ByDelYnAndStartDateBeforeAndEndDateAfterOrderByIsPinnedDescNnoDesc(
        String delYn, 
        LocalDateTime now1, 
        LocalDateTime now2
    );

    /**
     * 2. 사용자 목록용: 페이징 처리된 공지사항
     * 조건: 삭제되지 않음 AND 현재 게시 기간 내
     */
    Page<NoticeDto> findByDelYnAndStartDateBeforeAndEndDateAfter(
        String delYn, 
        LocalDateTime now1, 
        LocalDateTime now2, 
        Pageable pageable
    );

    /**
     * 3. 관리자용: 전체 공지사항 검색 (삭제 여부 상관없이 조회)
     */
    Page<NoticeDto> findByNtitleContaining(String ntitle, Pageable pageable);

    /**
     * 4. JPQL 활용: 특정 상태(게시 예정, 게시 중, 게시 종료)별 조회
     */
    @Query("SELECT n FROM NoticeDto n WHERE n.delYn = 'n' AND " +
           "(:status = 'READY' AND n.startDate > :now) OR " +
           "(:status = 'ING' AND n.startDate <= :now AND n.endDate >= :now) OR " +
           "(:status = 'END' AND n.endDate < :now)")
    List<NoticeDto> findNoticesByStatus(@Param("status") String status, @Param("now") LocalDateTime now);
}