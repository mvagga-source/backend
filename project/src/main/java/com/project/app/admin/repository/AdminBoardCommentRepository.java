package com.project.app.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.board.dto.BoardDto;
import com.project.app.boardcomment.dto.BoardCommentDto;

public interface AdminBoardCommentRepository extends JpaRepository<BoardCommentDto, Long> {

}
