package com.project.app.idea.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.project.app.common.exception.BaCdException;
import com.project.app.idea.dto.IdeaDto;

public interface IdeaService {
	
    public Slice<IdeaDto> getIdeaList(Pageable pageable) throws BaCdException; // 더보기 기능을 위한 Slice
    
    public IdeaDto save(IdeaDto dto) throws BaCdException;
    
    public List<IdeaDto> findAllByMemberId(String memberId) throws BaCdException;
    
}