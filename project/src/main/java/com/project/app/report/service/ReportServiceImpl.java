package com.project.app.report.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.common.exception.BaCdException;
import com.project.app.report.dto.ReportDto;
import com.project.app.report.repository.ReportRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class ReportServiceImpl implements ReportService {
	@Autowired
    private ReportRepository reportRepository;

    @Override
    public ReportDto save(ReportDto dto) throws BaCdException {
        dto.setStatus("신고대기");
        return reportRepository.save(dto);
    }

    @Override
    public void update(Long rno, String newStatus) {
        ReportDto report = reportRepository.findById(rno).orElseThrow(() -> new RuntimeException("신고 내역이 없습니다."));
        report.setStatus(newStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto> getReportsByTarget(String type, Long id) {
        return reportRepository.findByTargetTypeAndTargetId(type, id);
    }
}