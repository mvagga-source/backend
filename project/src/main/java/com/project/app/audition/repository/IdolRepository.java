package com.project.app.audition.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.IdolDto;
import com.project.app.audition.dto.IdolResponseDto;

public interface IdolRepository extends JpaRepository<IdolDto, Long> {

	// 1.해당 회차의 생존 아이돌 목록 조회 (탈락자 제외)
    List<IdolDto> findByAuditionAndStatus(AuditionDto audition, String status);
    
    // 2. 득표수 포함 아이돌 목록 조회(vote_detail + vote_bonus)
    //    득표수 내림차순 정렬
    @Query("""
            SELECT new com.project.app.audition.dto.IdolResponseDto(
                i.idolId,
                i.idolProfileId,
                i.status,
                (SELECT COUNT(vd.voteDetailId) FROM VoteDetailDto vd WHERE vd.idol = i)
    		    + (SELECT COALESCE(SUM(vb.bonusVotes), 0) FROM VoteBonusDto vb WHERE vb.idol = i AND vb.audition = i.audition),
                p.name,
                p.mainImgUrl
            )
            FROM IdolDto i
            LEFT JOIN IdolProfileDto p ON p.profileId = i.idolProfileId
            WHERE i.audition.auditionId = :auditionId
              AND i.status = 'active'
            ORDER BY (SELECT COUNT(vd.voteDetailId) FROM VoteDetailDto vd WHERE vd.idol = i)
    		       + (SELECT COALESCE(SUM(vb.bonusVotes), 0) FROM VoteBonusDto vb WHERE vb.idol = i AND vb.audition = i.audition) DESC
        """)
    List<IdolResponseDto> findIdolsWithVotes(@Param("auditionId") Long auditionId);

    // 3.실시간 랭킹 — 가산점(고정표수) 적용 후 내림차순
    @Query("""
	        SELECT i.idolId,
    			   p.name,
	               (SELECT COUNT(vd.voteDetailId) FROM VoteDetailDto vd WHERE vd.idol = i) AS rawVotes,
	               (SELECT COALESCE(SUM(vb.bonusVotes), 0) FROM VoteBonusDto vb WHERE vb.idol = i AND vb.audition = i.audition) AS totalBonus,
	               (SELECT COUNT(vd.voteDetailId) FROM VoteDetailDto vd WHERE vd.idol = i)
	               + (SELECT COALESCE(SUM(vb.bonusVotes), 0) FROM VoteBonusDto vb WHERE vb.idol = i AND vb.audition = i.audition) AS finalVotes,
	               i.idolProfileId
	        FROM IdolDto i
	        LEFT JOIN IdolProfileDto p ON p.profileId = i.idolProfileId
	        WHERE i.audition.auditionId = :auditionId
	          AND i.status = 'active'
	        ORDER BY finalVotes DESC
	    """)
    List<Object[]> findRankingByAuditionId(@Param("auditionId") Long auditionId);
    
	// 4.전체 참가자 조회 (탈락자 포함, status 조건 없음)
    @Query("""
    	    SELECT new com.project.app.audition.dto.IdolResponseDto(
    	        i.idolId,
    	        i.idolProfileId,
    	        i.status,
    	        COUNT(vd.voteDetailId),
    	        p.name,
    	        p.mainImgUrl
    	    )
    	    FROM IdolDto i
    	    LEFT JOIN IdolProfileDto p ON p.profileId = i.idolProfileId
    	    LEFT JOIN VoteDetailDto vd ON vd.idol = i
    	    WHERE i.audition.auditionId = :auditionId
    	    GROUP BY i.idolId, i.idolProfileId, i.status, p.name, p.mainImgUrl
    	    ORDER BY COUNT(vd.voteDetailId) DESC
    	""")
	List<IdolResponseDto> findAllIdolsWithVotes(@Param("auditionId") Long auditionId);
    
    // 5. 개인프로필 등수 및 투표 현황
    //    vote_detail 집계로 실시간 득표수 계산
    //    득표수 내림차순 정렬
    @Query("SELECT new com.project.app.audition.dto.IdolResponseDto(" +
 	       "i.idolId, i.idolProfileId, i.status, COUNT(vd), p.name, p.mainImgUrl) " +
 	       "FROM IdolDto i " +
 	       "LEFT JOIN IdolProfileDto p ON p.profileId = i.idolProfileId " +
 	       "LEFT JOIN VoteDetailDto vd ON vd.idol = i " +
 	       "WHERE i.audition.auditionId = :auditionId " +
 	       "AND i.idolProfileId = :idolProfileId " +
 	       "AND i.status = 'active' " + 
 	       "GROUP BY i.idolId, i.idolProfileId, i.status, p.name, p.mainImgUrl")
 	IdolResponseDto findIdolWithVote(@Param("auditionId") Long auditionId, @Param("idolProfileId") Long idolProfileId);
    
    // 6. IdolList용 — idol_profile 기준 최신 회차 status 포함 전체 참가자
    @Query("""
        SELECT new com.project.app.audition.dto.IdolResponseDto(
            i.idolId,
            i.idolProfileId,
            i.status,
            COUNT(vd.voteDetailId),
            p.name,
            p.mainImgUrl
        )
        FROM IdolDto i
        LEFT JOIN IdolProfileDto p ON p.profileId = i.idolProfileId
        LEFT JOIN VoteDetailDto vd ON vd.idol = i
        WHERE i.audition.auditionId = (
            SELECT MAX(i2.audition.auditionId)
            FROM IdolDto i2
            WHERE i2.idolProfileId = i.idolProfileId
            AND i2.audition.status IN ('ended', 'ongoing')
        )
        GROUP BY i.idolId, i.idolProfileId, i.status, p.name, p.mainImgUrl
        ORDER BY COUNT(vd.voteDetailId) DESC
    """)
    List<IdolResponseDto> findAllIdolsLatestStatus();
    
	// 특정 회차에 이미 등록된 idol 레코드가 존재하는지 확인 (중복 생성 방지용)
    boolean existsByAudition(AuditionDto audition);
}
