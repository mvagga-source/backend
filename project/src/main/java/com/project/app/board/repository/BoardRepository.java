package com.project.app.board.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.board.dto.BoardDto;

public interface BoardRepository extends JpaRepository<BoardDto, Long> {
	@Query(value = "select * from boarddto where bno=( select pre_bno from\r\n"
			+ "(select bno,lag(bno,1,-1) over(order by bno desc,bdate desc) pre_bno from boarddto) where bno=:bno)" ,nativeQuery = true)
	public Optional<BoardDto> findByPre(@Param("bno") Long bno);

	@Query(value = "select * from boarddto where bno=( select pre_bno from\r\n"
			+ "(select bno,lead(bno,1,-1) over(order by bno desc,bdate desc) pre_bno from boarddto) where bno=:bno)" ,nativeQuery = true)
	public Optional<BoardDto> findByNext(@Param("bno") Long bno);

	@Query(value = "SELECT b.*, " +
            " (SELECT bno FROM board WHERE bno < :bno ORDER BY bno DESC FETCH FIRST 1 ROWS ONLY) as prevBno, " +
            " (SELECT btitle FROM board WHERE bno < :bno ORDER BY bno DESC FETCH FIRST 1 ROWS ONLY) as prevTitle, " +
            " (SELECT bno FROM board WHERE bno > :bno ORDER BY bno ASC FETCH FIRST 1 ROWS ONLY) as nextBno, " +
            " (SELECT btitle FROM board WHERE bno > :bno ORDER BY bno ASC FETCH FIRST 1 ROWS ONLY) as nextTitle " +
            "FROM board b WHERE b.bno = :bno", nativeQuery = true)
    Optional<BoardDto> findDetail(@Param("bno") Long bno);

	//제목검색
	public Page<BoardDto> findByBtitleContaining(String btitle, Pageable pageable);

	//내용검색
	public Page<BoardDto> findByBcontentContaining(String bcontent, Pageable pageable);

	public Page<BoardDto> findByBtitleContainingOrBcontentContaining(String btitle, String bcontent, Pageable pageable);
}
