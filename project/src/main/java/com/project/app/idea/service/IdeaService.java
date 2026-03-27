package com.project.app.idea.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.common.exception.BaCdException;
import com.project.app.idea.dto.IdeaDto;

public interface IdeaService {
	
	public Map<String, Object> findAll(Long lastIdeano) throws BaCdException;
    
    public IdeaDto save(IdeaDto dto, MultipartFile file) throws BaCdException;
    
    public List<IdeaDto> findAllByMemberId(String memberId) throws BaCdException;
    
}