package com.project.app.report.service;

import java.util.List;

import com.project.app.common.exception.BaCdException;
import com.project.app.report.dto.ReportDto;

public interface ReportService {
	
	public ReportDto save(ReportDto dto) throws BaCdException;
	
	public void update(Long rno, String newStatus) throws BaCdException;
	
	public List<ReportDto> getReportsByTarget(String type, Long id) throws BaCdException;
}
