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
			select v.vote_date, v.audition_id, v.vote_id, p.PROFILEID, p.main_img_url, p.name, i.status
			from vote v
			join vote_detail d
			  on d.vote_id = v.vote_id
            join idol i
              on i.idol_id = d.idol_id
            join idol_profile p
              on i.idol_profile_id = p.profileid
            where v.member_id = :memberId 
            AND v.vote_date >= to_date(:startDate,'YYYY-MM-DD') and v.vote_date < to_date(:endDate,'YYYY-MM-DD') + 1
			order by v.vote_date desc, v.vote_id asc, d.idol_id asc
	""", nativeQuery = true)
	List<Map<String, Object>> findMyIdols(
			@Param("memberId") String memberId, 
			@Param("startDate") String startDate, 
			@Param("endDate") String endDate
			);


	@Query(
		value="""
			select * 
			from goods_orders g
			where g.id = :memberId
				AND g.crdt >= to_date(:startDate,'YYYY-MM-DD') and g.crdt < to_date(:endDate,'YYYY-MM-DD') + 1
		""",
	nativeQuery = true)
	Page<GoodsOrdersDto> findMyOrders(
			@Param("memberId") String memberId, 
			@Param("startDate") String startDate, 
			@Param("endDate") String endDate,			
			Pageable pageable);
	
	//판매내역
	@Query(value="""
	    SELECT go.* FROM goods g 
	    JOIN goods_orders go ON go.gno = g.gno
	    WHERE g.id = :memberId
	      AND go.crdt >= to_date(:startDate,'YYYY-MM-DD') 
	      AND go.crdt < to_date(:endDate,'YYYY-MM-DD') + 1
	    """,
	    countQuery = """
	    SELECT count(*) 
	    FROM goods g 
	    JOIN goods_orders go ON go.gno = g.gno
	    WHERE g.id = :memberId
	      AND go.crdt >= to_date(:startDate,'YYYY-MM-DD') 
	      AND go.crdt < to_date(:endDate,'YYYY-MM-DD') + 1
	    """,
	    nativeQuery = true)
	Page<GoodsOrdersDto> findMySale(
	        @Param("memberId") String memberId, 
	        @Param("startDate") String startDate, 
	        @Param("endDate") String endDate,            
	        Pageable pageable);
	
	
	@Query(
			value="""
				select * 
				from goods g
				where g.id = :memberId
				AND g.del_yn = 'n'
				AND g.crdt >= to_date(:startDate,'YYYY-MM-DD') and g.crdt < to_date(:endDate,'YYYY-MM-DD') + 1
			""",
		nativeQuery = true)
		Page<GoodsDto> findMyGoods(
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
			        WHEN b.pageType = 'VIDEO' THEN p.name
			        WHEN b.pageType = 'EVENT' THEN e.title
			        WHEN b.pageType = 'GOODS' THEN TO_CHAR(g.gimg)			        
			    END as name,
			    CASE 
			        WHEN b.pageType = 'VIDEO' THEN v.title
			        WHEN b.pageType = 'EVENT' THEN e.description
			        WHEN b.pageType = 'GOODS' THEN g.gname
			    END as title
			FROM Bookmark b
			LEFT JOIN Video v 
			    ON b.pageId = v.id
			LEFT JOIN Event e 
			    ON b.pageId = e.eno
			LEFT JOIN Goods g 
			    ON b.pageId = g.gno
			LEFT JOIN idol_profile p
			    on v.profileid = p.profileid
			WHERE b.memberId = :memberId 
			AND ( :pageType = 'ALL' OR b.pageType = :pageType )
			AND b.createdAt >= to_date(:startDate,'YYYY-MM-DD') and b.createdAt < to_date(:endDate,'YYYY-MM-DD') + 1 
			ORDER BY b.pageType ASC, b.createdAt DESC
			""", nativeQuery = true)		
	Page<Map<String, Object>> findMyBookmark(
			@Param("memberId") String memberId,
			@Param("pageType") String pageType,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate,
			Pageable pageable
			);

	
	@Query("SELECT b FROM BookmarkDto b WHERE b.memberId = :memberId and b.pageType = :pageType ")
	List<BookmarkDto> findMyPageBookmarks(
			@Param("memberId") String memberId, 
			@Param("pageType") PageType pageType
	);	
  
}
