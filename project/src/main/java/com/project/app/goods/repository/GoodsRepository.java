package com.project.app.goods.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.goods.dto.GoodsDto;

public interface GoodsRepository extends JpaRepository<GoodsDto, Long> {

	@Query("SELECT g FROM GoodsDto g WHERE g.delYn = 'n' " +
		       "AND (" +
		       "   (:category = 'gname' AND g.gname LIKE %:search%) OR " +       // 상품명만
		       "   (:category = 'gcontent' AND g.gcontent LIKE %:search%) OR " + // 아이돌로 나중에 교체
		       "   (:category = 'member' AND g.member.id LIKE %:search%) OR " +  // 판매자만
		       "   ((:category IS NULL OR :category = '') AND (" +              // 전체 검색 (카테고리 없을 때)
		       "       g.gname LIKE %:search% OR " +
		       "       g.gcontent LIKE %:search% OR " +				 // 아이돌로 나중에 교체
		       "       g.member.id LIKE %:search%" +
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
	
	public Page<GoodsDto> findByGnameContainingAndDelYn(String search, String string, Pageable pageable);
	
	public Page<GoodsDto> findByGcontentContainingAndDelYn(String search, String string, Pageable pageable);
	
	public Page<GoodsDto> findByDelYn(String string, Pageable pageable);

}
