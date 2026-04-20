package com.project.app.admin.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminGoodsRepository;
import com.project.app.admin.repository.AdminGoodsReviewRepository;
import com.project.app.admin.repository.AdminNoticeRepository;
import com.project.app.common.Common;
import com.project.app.common.GridUtils;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goods.dto.GoodsDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminGoodsServiceImpl implements AdminGoodsService {
	
	private final AdminGoodsOrdersRepository adminGoodsOrdersRepository;
	
	private final AdminGoodsRepository adminGoodsRepository;
	
	private final AdminGoodsReviewRepository adminGoodsReviewRepository;
	
	public Map<String, Object> getSummaryData() throws BaCdException {
        Map<String, Object> result = new HashMap<>();

        // 1. 오늘 매출
        Long todaySales = adminGoodsOrdersRepository.sumTodaySales();
        // 2. 배송대기
        Long readyCount = adminGoodsOrdersRepository.countByDelivStatus("배송대기");
        // 3. 판매중 상품
        Long activeGoodsCount = adminGoodsRepository.countByStatus("판매중");
        // 4. 취소요청
        Long cancelRequestCount = adminGoodsOrdersRepository.countByStatus("CANCEL");
        // 5. 평균 별점
        Double avgRating = adminGoodsReviewRepository.avgRating();
        // 6. 주간 매출
        List<Long> weeklySales = adminGoodsOrdersRepository.weeklySales();
        // 7. 아이돌별 판매 비중
        List<Object[]> idolStats = adminGoodsOrdersRepository.idolSalesRatio();
        List<String> idolLabels = new ArrayList<>();
        List<Long> idolValues = new ArrayList<>();

        for(Object[] row : idolStats){
            idolLabels.add((String) row[0]);
            idolValues.add((Long) row[1]);
        }

        // 8. 별점 분포
        List<Long> ratingDist = adminGoodsReviewRepository.ratingDistribution();

        result.put("todaySales", todaySales == null ? 0 : todaySales);
        result.put("readyCount", readyCount);
        result.put("activeGoodsCount", activeGoodsCount);
        result.put("cancelRequestCount", cancelRequestCount);
        result.put("avgRating", avgRating == null ? 0.0 : avgRating);
        result.put("weeklySales", weeklySales);
        result.put("idolLabels", idolLabels);
        result.put("idolValues", idolValues);
        result.put("ratingDistribution", ratingDist);

        return result;
    }
	
	@Override
	public Map<String, Object> goodsList(int page, int perPage, String category, String search, String status, String stockStatus, String isBanner,
			Long minPrice, Long maxPrice, String startDate, String endDate, String sortDir) {
		String sortColumn = "crdt"; // 기본 컬럼
    	Sort.Direction direction = Sort.Direction.DESC; // 기본 DESC
    	
    	LocalDateTime startDt = Common.parseDate(startDate, true);
    	LocalDateTime endDt = Common.parseDate(endDate, false);

    	// 시작일 > 종료일 체크
    	if (startDt != null && endDt != null && startDt.isAfter(endDt)) {
    	    throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "시작일은 종료일보다 클 수 없습니다.");
    	}

    	if (sortDir != null && !sortDir.isEmpty()) {
    	    String[] parts = sortDir.split("_");
    	    if (parts.length == 2) {
    	        String col = parts[0];
    	        String dir = parts[1];
    	        // 컬럼 매핑 (DB 컬럼/alias 확인)
    	        switch (col) {
    	            case "crdt": sortColumn = "crdt"; break;           // 주문일
    	            case "price": sortColumn = "total_price"; break;  // 금액
    	            case "rating": sortColumn = "avgRating"; break;   // 평점 (alias)
    	            case "helpful": sortColumn = "reviewCnt"; break;  // 리뷰수 (alias)
    	        }
    	        direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    	    }
    	}

    	Sort sort;
    	if ("avgRating".equals(sortColumn) || "reviewCnt".equals(sortColumn)) {
    	    // alias 정렬 불가 → Java에서 Stream 정렬 처리
    	    sort = Sort.unsorted();
    	} else {
    	    sort = Sort.by(direction, sortColumn);
    	}
		
		Pageable pageable = PageRequest.of(page - 1, perPage);
	    
	    // sortDir 예시: "rating_desc", "helpful_desc", "gno_desc"
	    Page<Map<String, Object>> resultPage = adminGoodsRepository.findAdminGoodsMap(
	            search, category, status, stockStatus, isBanner, minPrice, maxPrice, startDate, endDate, sortDir, pageable
	    );

	    return GridUtils.gridRes(resultPage, perPage);
	}
	
	@Override
	@Transactional
	public Map<String, Object> updateGoodsItems(List<Map<String, Object>> updatedRows) {
	    for (Map<String, Object> row : updatedRows) {
	        Long gno = Long.parseLong(String.valueOf(row.get("gno")));
	        GoodsDto goods = adminGoodsRepository.findById(gno)
	                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND));

	        if (row.containsKey("gname")) goods.setGname((String) row.get("gname"));
	        //if (row.containsKey("price")) goods.setPrice((long) Integer.parseInt(String.valueOf(row.get("price"))));
	        // --- 가격 수정 제약 로직 추가 ---
	        if (row.containsKey("price")) {
	            long newPrice = Long.parseLong(String.valueOf(row.get("price")));
	            
	            // 기존 가격과 다를 경우에만 체크
	            if (goods.getPrice() != newPrice) {
	                // 해당 상품(gno)으로 들어온 주문이 있는지 확인
	                // adminGoodsOrdersRepository에 countByGoods_Gno 또는 유사한 메서드가 있어야 합니다.
	                long orderCount = adminGoodsOrdersRepository.countByGoods_Gno(gno);		//시간이 촉박하여 주문실패나 주문취소까지는 고려X(반정규화로 수정시 다른 메인 화면 등 수정할 부분이 많음)
	                
	                if (orderCount > 0) {
	                    // 주문이 있다면 예외 발생 (또는 에러 메시지 반환)
	                    throw new BaCdException(ErrorCode.IS_EXIST, "이미 주문이 존재하는 상품은 가격을 수정할 수 없습니다.");
	                }
	                goods.setPrice(newPrice);
	            }
	        }
	        if (row.containsKey("stockCnt")) goods.setStockCnt((long) Integer.parseInt(String.valueOf(row.get("stockCnt"))));
	        if (row.containsKey("status")) goods.setStatus((String) row.get("status"));
	        if (row.containsKey("isBanner")) goods.setIsBanner((String) row.get("isBanner"));
	        if (row.containsKey("bannerSort")) goods.setBannerSort(Integer.parseInt(String.valueOf(row.get("bannerSort"))));
	        
	        adminGoodsRepository.save(goods);
	    }
	    Map<String, Object> map = new HashMap<>();
	    map.put("result", true);
	    return map;
	}
}
