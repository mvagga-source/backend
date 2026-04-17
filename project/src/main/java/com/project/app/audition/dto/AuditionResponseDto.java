package com.project.app.audition.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditionResponseDto {//백엔드가 프론트에 회차 목록을 내려줄 때 사용하는 응답전용 DTO
	
	private Long      auditionId;	 // 회차 PK (API 호출 시 사용)
    private Integer   round;         // 회차 번호 (탭에 "1차", "2차" 표시)
    private String    title;         // 제목 ("1차 오디션")
    private LocalDate startDate;
    private LocalDate endDate;
    private String    status;        // 진행상태("ended"마감 / "ongoing"진행 / "upcoming"준비)
    private Boolean   hasTeamMatch;  // 팀경연 여부
    private Boolean   hasMatchDone;  // 결과확정 여부
    private Integer   survivorCount; // 커트라인 생존자 수

}
