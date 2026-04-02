package com.project.app.idea.service;

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
import com.project.app.idea.dto.IdeaDto;
import com.project.app.idea.repository.IdeaRepository;
import com.project.app.report.dto.ReportDto;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class IdeaServiceImpl implements IdeaService {
	
	@Autowired
    private IdeaRepository ideaRepository;

	@Override
	public Map<String, Object> findAll(Long lastIdeano) throws BaCdException {
		Pageable pageable = PageRequest.of(0, 10);
		// 처음 요청 시 가장 큰 값으로 세팅
	    long queryId = (lastIdeano == null || lastIdeano == 0) ? Long.MAX_VALUE : lastIdeano;
	    
	    // 전체 대상 조회
	    Slice<IdeaDto> slice = ideaRepository.findNextPageAll(queryId, pageable);
	    
	    // 전체 카운트
	    long totalCount = ideaRepository.count();
        
        Map<String, Object> map = new HashMap<>();
        map.put("list", slice.getContent());
        map.put("hasNext", slice.hasNext()); // 다음 페이지 존재 여부 전달
        map.put("totalCount", totalCount); // 전체 개수 추가
    	return map;
	}
	
    @Transactional
    @Override
    public IdeaDto save(IdeaDto dto, MultipartFile file) {
    	if (file != null && !file.isEmpty()) {
            String filePath = Common.saveFile(file, "idea");
            dto.setIdeafile(filePath);
        }
        return ideaRepository.save(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdeaDto> findAllByMemberId(String memberId) {
        return ideaRepository.findByMember_IdOrderByCrdtDesc(memberId);
    }
}
