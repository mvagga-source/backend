package com.project.app.audition.dto;

import java.util.List;

import lombok.Data;

@Data
public class VoteRequestDto {

    // 어느 회차에 투표하는지
    private Long auditionId;

    // 선택한 아이돌 id 목록 (최대 7명)
    private List<Long> idolIds;
}
