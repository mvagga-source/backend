package com.project.app.idea.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.common.exception.BaCdException;
import com.project.app.idea.dto.IdeaDto;
import com.project.app.idea.repository.IdeaRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class IdeaServiceImpl implements IdeaService {
	@Autowired
    private IdeaRepository ideaRepository;

    @Override
    @Transactional(readOnly = true)
    public Slice<IdeaDto> getIdeaList(Pageable pageable) {
        return ideaRepository.findAllByOrderByCrdtDesc(pageable);
    }

    @Override
    public IdeaDto save(IdeaDto dto) {
        return ideaRepository.save(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdeaDto> findAllByMemberId(String memberId) {
        return ideaRepository.findByMember_IdOrderByCrdtDesc(memberId);
    }
}
