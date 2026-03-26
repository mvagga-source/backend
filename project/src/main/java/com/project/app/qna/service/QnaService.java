package com.project.app.qna.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.project.app.common.exception.BaCdException;
import com.project.app.qna.dto.QnaDto;

public interface QnaService {
	
    public QnaDto save(QnaDto dto) throws BaCdException;
    
    public Map<String, Object> findAllByMemberId(String memberId, Long lastQno) throws BaCdException;
    
    public QnaDto answerQna(Long qno, String answerContent) throws BaCdException; // 관리자 답변
    
    public void deleteQna(Long qno) throws BaCdException; // 논리 삭제 (del_yn = 'y')
}