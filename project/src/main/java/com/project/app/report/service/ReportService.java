package com.project.app.report.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.project.app.common.exception.BaCdException;
import com.project.app.report.dto.ReportDto;

public interface ReportService {
	
	public ReportDto save(ReportDto dto, MultipartFile repofile) throws BaCdException;
	
	public void update(Long rno, String newStatus) throws BaCdException;

	public Map<String, Object> findAll(Long lastRepono) throws BaCdException;
	
	//public List<ReportDto> getReportsByTarget(String type, Long id) throws BaCdException;
}
