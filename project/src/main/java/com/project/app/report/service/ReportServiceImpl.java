package com.project.app.report.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.common.Common;
import com.project.app.common.exception.BaCdException;
import com.project.app.qna.dto.QnaDto;
import com.project.app.report.dto.ReportDto;
import com.project.app.report.repository.ReportRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class ReportServiceImpl implements ReportService {
	
	@Autowired
    private ReportRepository reportRepository;
	
	@Override
	public Map<String, Object> findAll(Long lastRepono) throws BaCdException {
		Pageable pageable = PageRequest.of(0, 10);
		// 처음 요청 시 가장 큰 값으로 세팅
	    long queryId = (lastRepono == null || lastRepono == 0) ? Long.MAX_VALUE : lastRepono;
	    
	    // 전체 대상 조회
	    Slice<ReportDto> slice = reportRepository.findNextPageAll(queryId, pageable);
	    
	    // 전체 카운트
	    long totalCount = reportRepository.count();
        
        Map<String, Object> map = new HashMap<>();
        map.put("list", slice.getContent());
        map.put("hasNext", slice.hasNext()); // 다음 페이지 존재 여부 전달
        map.put("totalCount", totalCount); // 전체 개수 추가
    	return map;
	}

    @Override
    public ReportDto save(ReportDto dto, MultipartFile repofile) throws BaCdException {
        dto.setStatus("신고대기");
        if (repofile != null && !repofile.isEmpty()) {
            String filePath = Common.saveFile(repofile, "report");
            dto.setRepofile(filePath);
        }
        return reportRepository.save(dto);
    }

    @Override
    public void update(Long rno, String newStatus) {
        ReportDto report = reportRepository.findById(rno).orElseThrow(() -> new RuntimeException("신고 내역이 없습니다."));
        report.setStatus(newStatus);
    }

    /*@Override
    @Transactional(readOnly = true)
    public List<ReportDto> getReportsByTarget(String type, Long id) {
        return reportRepository.findByTargetTypeAndTargetId(type, id);
    }*/
}