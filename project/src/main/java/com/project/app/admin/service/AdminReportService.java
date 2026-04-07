package com.project.app.admin.service;

import java.util.Map;

import com.project.app.report.dto.ReportDto;

public interface AdminReportService {

	Map<String, Object> ajaxList(int page, int perPage, String reportType, String category, String search,
			String status, String startDate, String endDate);

	ReportDto view(Long repono);

}
