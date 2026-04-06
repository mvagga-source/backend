package com.project.app.audition.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamMatchResponseDto {//팀경연 결과를 프론트에 내려줄 때 사용하는 응답 전용 DTO

	private Long       matchId;		  // 대결 식별 아이디
    private String     matchName;     // 대결 조 이름 ("1조", "2조")
 
    private Long       teamAId;		  // A팀 정보
    private String     teamAName;     // "A팀"
    private String	   teamAImgUrl;   // A팀 대표 이미지 URL
    
    private Long       teamBId;		  // B팀 정보
    private String     teamBName;     // "B팀"
    private String	   teamBImgUrl;   // B팀 대표 이미지 URL
 
    private BigDecimal teamAScore;    // A팀 득표율 ex) 55.00
    private BigDecimal teamBScore;    // B팀 득표율 ex) 45.00
 
    private Long       winnerTeamId;  // 승리팀 id (null = 미결정)
    private String     status;        // 대결 상태 ("pending"준비 / "done"종료)
 
    private List<String> membersA;    // A팀 멤버 이름 목록
    private List<String> membersB;    // B팀 멤버 이름 목록
}
