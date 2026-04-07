package com.project.app.admin.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminIdeaRepository;
import com.project.app.admin.repository.AdminReportRepository;
import com.project.app.common.exception.BaCdException;
import com.project.app.report.dto.ReportDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminReportServiceImpl implements AdminReportService {
	private final AdminReportRepository adminReportRepository;

    @Override
    public Map<String, Object> ajaxList(int page, int perPage, String reportType, String category, 
                                       String search, String status, String startDate, String endDate) {
        
        PageRequest pageRequest = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "repono"));

        // 날짜 처리
        java.sql.Timestamp start = (startDate != null && !startDate.isEmpty()) ? java.sql.Timestamp.valueOf(startDate + " 00:00:00") : null;
        java.sql.Timestamp end = (endDate != null && !endDate.isEmpty()) ? java.sql.Timestamp.valueOf(endDate + " 23:59:59") : null;

        // 파라미터 정제
        String type = (reportType != null && !reportType.isEmpty()) ? reportType : null;
        String stat = (status != null && !status.isEmpty()) ? status : null;
        String kw = (search != null && !search.isEmpty()) ? search : null;

        Page<ReportDto> reportPage = adminReportRepository.findByFilters(type, category, kw, stat, start, end, pageRequest);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        data.put("contents", reportPage.getContent());
        data.put("pagination", Map.of("page", page, "totalCount", reportPage.getTotalElements()));
        
        result.put("result", true);
        result.put("data", data);

        return result;
    }

    @Override
    public ReportDto view(Long repono) {
        return adminReportRepository.findById(repono)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 내역이 없습니다. repono=" + repono));
    }
}
