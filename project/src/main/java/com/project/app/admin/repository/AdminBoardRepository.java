package com.project.app.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.board.dto.BoardDto;
import com.project.app.goods.dto.GoodsDto;

public interface AdminBoardRepository extends JpaRepository<BoardDto, Long> {

}
