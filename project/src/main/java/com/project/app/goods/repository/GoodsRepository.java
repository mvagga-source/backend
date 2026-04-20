package com.project.app.goods.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goods.dto.GoodsDto;

public interface GoodsRepository extends JpaRepository<GoodsDto, Long> {

	@Query("SELECT g FROM GoodsDto g " +
				"LEFT JOIN g.idol i " + // IdolProfileDto와 조인
				"WHERE g.delYn = 'n' " +
		       "AND (" +
		       "   (:category = 'gname' AND g.gname LIKE %:search%) OR " +       // 상품명만
		       "   (:category = 'idol' AND i.name LIKE %:search%) OR " +  // 참가자 이름 검색
		       "   (:category = 'member' AND g.member.nickname LIKE %:search%) OR " +  // 판매자만
		       "   ((:category IS NULL OR :category = '') AND (" +              // 전체 검색 (카테고리 없을 때)
		       "       g.gname LIKE %:search% OR " +
		       "       i.name LIKE %:search% OR " +
		       "       g.member.nickname LIKE %:search%" +
		       "   ))" +
		       ") " +
		       // minPrice가 0이면 무조건 true(패스), 아니면 그 이상의 가격만 조회
		       "AND (:minPrice = 0 OR g.price >= :minPrice) " +
		       // maxPrice가 0이면 무조건 true(패스), 아니면 그 이하의 가격만 조회
		       "AND (:maxPrice = 0 OR g.price <= :maxPrice)")
		Page<GoodsDto> findGoodsWithFilters(
		    @Param("category") String category, 
		    @Param("search") String search, 
		    @Param("minPrice") int minPrice, 
		    @Param("maxPrice") int maxPrice, 
		    Pageable pageable);

	
	@Query("SELECT g FROM GoodsDto g " +
			"LEFT JOIN g.idol i " + // IdolProfileDto와 조인
			"WHERE g.delYn = 'n' AND g.member.id = :id " +
	       "AND (" +
	       "   (:category = 'gname' AND g.gname LIKE %:search%) OR " +       // 상품명만
	       "   (:category = 'idol' AND i.name LIKE %:search%) OR " +  // 참가자 이름 검색
	       "   (:category = 'member' AND g.member.nickname LIKE %:search%) OR " +  // 판매자만
	       "   ((:category IS NULL OR :category = '') AND (" +              // 전체 검색 (카테고리 없을 때)
	       "       g.gname LIKE %:search% OR " +
	       "       i.name LIKE %:search% OR " +
	       "       g.member.nickname LIKE %:search%" +
	       "   ))" +
	       ") " +
	       // minPrice가 0이면 무조건 true(패스), 아니면 그 이상의 가격만 조회
	       "AND (:minPrice = 0 OR g.price >= :minPrice) " +
	       // maxPrice가 0이면 무조건 true(패스), 아니면 그 이하의 가격만 조회
	       "AND (:maxPrice = 0 OR g.price <= :maxPrice)")
	Page<GoodsDto> findMyGoodsWithFilters(
		    @Param("category") String category, 
		    @Param("search") String search, 
		    @Param("minPrice") int minPrice, 
		    @Param("maxPrice") int maxPrice, 
			Pageable pageable,
			@Param("id") String id);
	
	
	
	public Page<GoodsDto> findByGnameContainingAndDelYn(String search, String string, Pageable pageable);
	
	public Page<GoodsDto> findByGcontentContainingAndDelYn(String search, String string, Pageable pageable);
	
	public Page<GoodsDto> findByDelYn(String string, Pageable pageable);
	
	/**
	 * 굿즈 배너(평균 별점 높은 순)
	 * @param pageable
	 * @return
	 */
	@Query(value = 
		    "SELECT g.gno, g.gname, TO_CHAR(g.gimg) as gimg, g.price, m.nickname as sellerId, " +
		    "       COALESCE(ROUND(AVG(r.rating), 2), 0.00) as avgRating, " + // 소수점 2자리 반올림
		    "       COUNT(r.grno) as reviewCnt " +
		    "FROM goods g " +
		    "JOIN member m ON g.id = m.id " +
		    "LEFT JOIN goods_review r ON g.gno = r.gno AND r.del_yn = 'n' " +
		    "WHERE g.del_yn = 'n' AND g.is_banner = 'y' " +
		    "GROUP BY g.gno, g.gname, TO_CHAR(g.gimg), g.price, m.nickname, g.banner_sort " +
		    //"HAVING COUNT(r.grno) > 0 " +
		    "ORDER BY g.banner_sort ASC, AVG(r.rating) DESC, COUNT(r.grno) DESC, g.gno DESC", 
		    nativeQuery = true)
		List<Map<String, Object>> findTopRatedBannerList(Pageable pageable);


}
