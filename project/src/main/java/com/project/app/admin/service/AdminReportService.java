package com.project.app.admin.service;

import java.util.Map;

import com.project.app.common.exception.BaCdException;
import com.project.app.report.dto.ReportDto;

public interface AdminReportService {

	Map<String, Object> ajaxList(int page, int perPage, String reportType, String category, String search,
			String status, String startDate, String endDate) throws BaCdException;

	ReportDto view(Long repono) throws BaCdException;

	Map<String, Object> ajaxUpdateStatus(Long repono, String status, String targetIdName, Long targetId) throws BaCdException;

}
