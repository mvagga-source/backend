package com.project.app.mypage.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.bookmark.dto.BookmarkDto;
import com.project.app.bookmark.dto.PageType;
import com.project.app.goods.dto.GoodsDto;
import com.project.app.goodsorders.dto.GoodsOrdersDto;
import com.project.app.mypage.dto.MypageDto;

public interface MypageRepository extends JpaRepository<MypageDto, Long> {

	@Query(value="""
			select v.vote_date, v.audition_id, v.voteid, p.PROFILEID, p.main_img_url, p.name, i.status
			from vote v
			join vote_detail d
			  on d.vote_id = v.voteid
            join idol i
              on i.idolid = d.idol_id
            join idol_profile p
              on i.idol_profile_id = p.profileid
            where v.member_id = :memberId and v.vote_date between :startDate and :endDate
			order by v.vote_date desc, v.voteid asc, d.idol_id asc
	""", nativeQuery = true)
	List<Map<String, Object>> findMyIdols(
			@Param("memberId") String memberId, 
			@Param("startDate") String startDate, 
			@Param("endDate") String endDate
			);

	
	@Query(
		value="""
			select g.* 
			from goods_orders g
			where g.id = :memberId
			and g.crdt between to_date(:startDate) and to_date(:endDate)
		""",
	nativeQuery = true)
	Page<GoodsOrdersDto> findMyOrders(
			@Param("memberId") String memberId, 
			@Param("startDate") String startDate, 
			@Param("endDate") String endDate,			
			Pageable pageable);
	
	
	@Query(
			value="""
				select g.* 
				from goods g
				where g.id = :memberId
				and g.crdt between to_date(:startDate) and to_date(:endDate)
			""",
		nativeQuery = true)
		Page<GoodsDto> findMySales(
				@Param("memberId") String memberId, 
				@Param("startDate") String startDate, 
				@Param("endDate") String endDate,			
				Pageable pageable);


	
	@Query(value="""
			select
			    b.id,
			    b.pageType,
			    b.createdAt,
			    b.pageId,
			    CASE 
			        WHEN b.pageType = 'VIDEO' THEN v.name
			        WHEN b.pageType = 'EVENT' THEN e.description
			        WHEN b.pageType = 'GOODS' THEN TO_CHAR(g.gimg)			        
			    END as name,
			    CASE 
			        WHEN b.pageType = 'VIDEO' THEN v.title
			        WHEN b.pageType = 'EVENT' THEN e.title
			        WHEN b.pageType = 'GOODS' THEN g.gname
			    END as title
			FROM Bookmark b
			LEFT JOIN Video v 
			    ON b.pageId = v.id
			LEFT JOIN Event e 
			    ON b.pageId = e.eno
			LEFT JOIN Goods g 
			    ON b.pageId = g.gno    
			WHERE b.memberId = :memberId 
			AND ( :pageType = 'ALL' OR b.pageType = :pageType )
			AND b.createdAt between to_date(:startDate) and to_date(:endDate) 
			ORDER BY b.pageType ASC, b.createdAt,'YYYY-MM-DD' DESC
			""", nativeQuery = true)		
	List<Map<String, Object>> findMyBookmark(
			@Param("memberId") String memberId,
			@Param("pageType") String pageType,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate
			);

	
	@Query("SELECT b FROM BookmarkDto b WHERE b.memberId = :memberId and b.pageType = :pageType ")
	List<BookmarkDto> findMyPageBookmarks(
			@Param("memberId") String memberId, 
			@Param("pageType") PageType pageType
	);	
  
}
