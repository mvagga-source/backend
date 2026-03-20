package com.project.app.boardlike.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.boardlike.dto.BoardLikeDto;

public interface BoardLikeRepository extends JpaRepository<BoardLikeDto, Long> {

	public Optional<BoardLikeDto> findByBoardBnoAndMemberId(Long bno, String id);

}
