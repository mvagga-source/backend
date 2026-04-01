package com.project.app.mypage.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.audition.dto.VoteDto;
import com.project.app.mypage.dto.MyVoteResponse;

public interface MypageRepository extends JpaRepository<VoteDto, Long> {

	@Query(value="""
			select v.vote_date, v.voteid, d.idol_id, p.main_img_url, p.name, i.status
			from vote v
			join vote_detail d
			  on d.vote_id = v.voteid
            join idol i
              on i.idolid = d.idol_id
            join idol_profile p
              on i.idol_profile_id = p.profileid
            where v.member_id = :memberId
			order by v.vote_date desc, v.voteid asc, d.idol_id asc
	""", nativeQuery = true)
	List<Map<String, Object>> findIdolsById(@Param("memberId") String memberId);
	
	

}
