package com.project.app.board.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.board.dto.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
	@Query(value = "select * from boarddto where bno=( select pre_bno from\r\n"
			+ "(select bno,lag(bno,1,-1) over(order by bgroup desc,bstep asc) pre_bno from boarddto) where bno=:bno)" ,nativeQuery = true)
	public Optional<Board> findByPre(@Param("bno") Long bno);

	@Query(value = "select * from boarddto where bno=( select pre_bno from\r\n"
			+ "(select bno,lead(bno,1,-1) over(order by bgroup desc,bstep asc) pre_bno from boarddto) where bno=:bno)" ,nativeQuery = true)
	public Optional<Board> findByNext(@Param("bno") Long bno);

	//제목검색
	public Page<Board> findByBtitleContaining(String btitle, Pageable pageable);

	//내용검색
	public Page<Board> findByBcontentContaining(String bcontent, Pageable pageable);

	public Page<Board> findByBtitleContainingOrBcontentContaining(String btitle, String bcontent, Pageable pageable);
}
