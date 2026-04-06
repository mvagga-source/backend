package com.project.app.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminGoodsRepository;
import com.project.app.admin.repository.AdminGoodsReviewRepository;
import com.project.app.admin.repository.AdminNoticeRepository;
import com.project.app.common.exception.BaCdException;

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
}
