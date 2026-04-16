package com.project.app.admin.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminBoardCommentRepository;
import com.project.app.admin.repository.AdminBoardRepository;
import com.project.app.admin.repository.AdminIdeaRepository;
import com.project.app.admin.repository.AdminReportRepository;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.report.dto.ReportDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminReportServiceImpl implements AdminReportService {
	private final AdminReportRepository adminReportRepository;
	
	private final AdminBoardRepository adminBoardRepository;
	
	private final AdminBoardCommentRepository adminBoardCommentRepository;

    @Override
    public Map<String, Object> ajaxList(int page, int perPage, String reportType, String category, 
                                       String search, String status, String startDate, String endDate) {
        
        PageRequest pageRequest = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "repono"));

        // 날짜 처리
        java.sql.Timestamp start = (startDate != null && !startDate.isEmpty()) ? java.sql.Timestamp.valueOf(startDate + " 00:00:00") : null;
        java.sql.Timestamp end = (endDate != null && !endDate.isEmpty()) ? java.sql.Timestamp.valueOf(endDate + " 23:59:59") : null;
        // 날짜 검증 추가
	    if (start != null && end != null) {
	    	if(start.after(end)) {
	    		throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "시작일은 종료일보다 클 수 없습니다.");
	    	}
	    }

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

	@Override
	public Map<String, Object> ajaxUpdateStatus(Long repono, String status, String targetIdName, Long targetId) throws BaCdException {
		Map<String, Object> result = new HashMap<>();
		
		ReportDto report = adminReportRepository.findById(repono).orElseThrow(() -> new IllegalArgumentException("해당 신고 내역이 없습니다. repono=" + repono));
		report.setStatus(status);
		// 관리자가 수동으로 입력했을 수도 있으므로 신고 엔티티의 target 정보도 함께 업데이트
	    report.setTargetIdName(targetIdName);
	    report.setTargetId(targetId);
	    
	    // 상태에 따른 원문 블라인드 처리 분기
	    // '처리완료'일 때만 'y' (숨김), 그 외 모든 상태('신고대기', '신고반려')는 'n' (노출)
	    String reportYnVal = "처리완료".equals(status) ? "y" : "n";

	    if (targetId != null && targetId > 0) {
	        if ("bno".equals(targetIdName)) {
	            // 게시글(Board) 처리
	            adminBoardRepository.findById(targetId).ifPresent(board -> {
	                board.setReportYn(reportYnVal);
	                adminBoardRepository.save(board);
	            });
	        } else if ("cno".equals(targetIdName)) {
	            // 댓글(Comment) 처리
	            adminBoardCommentRepository.findById(targetId).ifPresent(comment -> {
	                comment.setReportYn(reportYnVal);
	                adminBoardCommentRepository.save(comment);
	            });
	        }
	    }
		result.put("result", adminReportRepository.save(report));
		return result;
	}
}
