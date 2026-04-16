package com.project.app.admin.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminIdeaRepository;
import com.project.app.admin.repository.AdminQnaRepository;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.idea.dto.IdeaDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminIdeaServiceImpl implements AdminIdeaService {
	private final AdminIdeaRepository adminIdeaRepository;

	@Override
	public Map<String, Object> ajaxList(int page, int perPage, String category, String ideacategory, String search, String status,
	        String startDate, String endDate) throws BaCdException {
	    
	    // 1. 페이징 및 정렬 설정 (최신순)
	    PageRequest pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "ideano"));
	
	    // 2. 날짜 문자열 처리 (빈 문자열일 경우 null 처리)
	    Timestamp startDated = (startDate != null && !startDate.isEmpty()) ? Timestamp.valueOf(startDate + " 00:00:00") : null;
	    Timestamp endDated = (endDate != null && !endDate.isEmpty()) ? Timestamp.valueOf(endDate + " 23:59:59") : null;
	    // 날짜 검증 추가
	    if (startDated != null && endDated != null) {
	    	if(startDated.after(endDated)) {
	    		throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "시작일은 종료일보다 클 수 없습니다.");
	    	}
	    }
	
	    // 3. 데이터 조회
	    Page<IdeaDto> ideaPage = adminIdeaRepository.findByFilters(ideacategory, category, search, startDated, endDated, pageable);
	
	    // 4. TUI Grid 규격에 맞게 Map 구성
	    Map<String, Object> result = new HashMap<>();
	    Map<String, Object> data = new HashMap<>();
	    Map<String, Object> pagination = new HashMap<>();
	
	    pagination.put("page", page);
	    pagination.put("totalCount", ideaPage.getTotalElements());
	
	    data.put("contents", ideaPage.getContent());
	    data.put("pagination", pagination);
	
	    result.put("result", true);
	    result.put("data", data);
	
	    return result;
	}
	
	@Override
	public IdeaDto view(Long ideano) {
	    return adminIdeaRepository.findById(ideano)
	            .orElseThrow(() -> new IllegalArgumentException("해당 아이디어가 존재하지 않습니다. id=" + ideano));
	}
	
	@Override
	public Map<String, Object> saveFeedback(Map<String, Object> param) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        Long ideano = Long.parseLong(param.get("ideano").toString());
	        String feedback = (String) param.get("adminFeedback");
	
	        Optional<IdeaDto> ideaOpt = adminIdeaRepository.findById(ideano);
	        if (ideaOpt.isPresent()) {
	            IdeaDto idea = ideaOpt.get();
	            // IdeaDto에 adminFeedback 필드가 있다면 세팅 (현재 DTO에는 없으므로 필요시 추가 필요)
	            // idea.setAdminFeedback(feedback); 
	            adminIdeaRepository.save(idea);
	            
	            response.put("success", true);
	        } else {
	            response.put("success", false);
	            response.put("message", "아이디어를 찾을 수 없습니다.");
	        }
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "저장 중 오류 발생: " + e.getMessage());
	    }
	    
	    return response;
	}

}
