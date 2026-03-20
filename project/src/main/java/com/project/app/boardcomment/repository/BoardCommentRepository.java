package com.project.app.boardcomment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.board.dto.BoardDto;
import com.project.app.boardcomment.dto.BoardCommentDto;

public interface BoardCommentRepository extends JpaRepository<BoardCommentDto, Long> {

	// 특정 게시글의 댓글을 그룹 순서(cgroup)와 그룹 내 순서(cstep)에 맞춰 조회
    List<BoardCommentDto> findByBoardBnoOrderByCgroupAscCstepAsc(Long bno);

    // 답글 삽입 전, 기존 답글들의 순서를 뒤로 밀기 (계층형 로직)
    @Modifying
    @Query("UPDATE BoardCommentDto c SET c.cstep = c.cstep + 1 WHERE c.cgroup = :cgroup AND c.cstep > :cstep")
    void updateCstep(@Param("cgroup") Long cgroup, @Param("cstep") Long cstep);

    // 가장 높은 cgroup 번호 찾기 (새로운 원댓글 작성 시 필요)
    @Query("SELECT COALESCE(MAX(c.cgroup), 0) FROM BoardCommentDto c")
    Long findMaxCgroup();

	// 특정 게시글의 댓글을 최신순으로 페이징 (처음 로딩용)
	List<BoardCommentDto> findByBoardBno(Long bno, Pageable pageable);

	// 마지막으로 본 cno보다 작은(이전) 데이터들을 가져옴 (최신순)
    List<BoardCommentDto> findByBoardBnoAndCnoLessThan(Long bno, Long lastCno, Pageable pageable);

	public BoardDto save(BoardDto bdto);

	public void deleteById(Long bno);

	@Query(value = "SELECT * FROM board_comment " +
           "WHERE bno = :bno AND cgroup IN (" +
           "  SELECT cgroup FROM (" +
           "    SELECT DISTINCT cgroup FROM board_comment " +
           "    WHERE bno = :bno " +
           "    AND (:lastGroup = 0 OR cgroup < :lastGroup) " +
           "    ORDER BY cgroup DESC" +
           "  ) WHERE ROWNUM <= :size" +
           ") " +
           "ORDER BY cgroup DESC, cstep ASC", nativeQuery = true)
    List<BoardCommentDto> findCommentsByGroupPaging(@Param("bno") Long bno, @Param("lastGroup") Long lastGroup, @Param("size") int size);

	// cindent가 0인 것(원글)만 카운트
	Long countByBoardBnoAndDelYnAndCindent(Long bno, String delYn, Long cindent);

	public void deleteByBoard(BoardDto bdto);
}
