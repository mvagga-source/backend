package com.project.app.qna.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.qna.dto.QnaDto;
import com.project.app.qna.repository.QnaRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class QnaServiceImpl implements QnaService {
    @Autowired
	private QnaRepository qnaRepository;

    /**
     * 목록조회
     */
    @Override
    public Map<String, Object> findAllByMemberId(String memberId, Long lastQno) throws BaCdException {
    	// 10개씩 가져오도록 설정
        Pageable pageable = PageRequest.of(0, 10); 
        
        // 처음 요청 시 lastQno가 0이라면 아주 큰 값으로 대체
        long queryQno = (lastQno == null || lastQno == 0) ? Long.MAX_VALUE : lastQno;
        
        Slice<QnaDto> slice = qnaRepository.findNextPage(memberId, queryQno, pageable);
        
        // 2. 전체 개수 조회
        long totalCount = qnaRepository.countByMemberIdAndDelYn(memberId, "n");
        
        Map<String, Object> map = new HashMap<>();
        map.put("list", slice.getContent());
        map.put("hasNext", slice.hasNext()); // 다음 페이지 존재 여부 전달
        map.put("totalCount", totalCount); // 전체 개수 추가
    	return map;
    }
    
    /**
     * 문의글 저장
     */
    @Transactional
    @Override
    public QnaDto save(QnaDto dto) {
        dto.setStatus("답변대기");
        dto.setDelYn("n");
        return qnaRepository.save(dto);
    }
    
    /**
     * 문의글 수정
     */
    @Transactional
    @Override
    public QnaDto update(QnaDto dto, MemberDto member) {
    	QnaDto qna = qnaRepository.findById(dto.getQno()).filter(q -> "n".equals(q.getDelYn())) // 삭제되지 않은 것만
                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "문의글을 찾을 수 없습니다."));
        if ("답변완료".equals(qna.getStatus())) {
            throw new BaCdException(ErrorCode.COMPLETE_STATUS, "답변 완료된 문의는 수정할 수 없습니다.");
        }
        
        qna.setQtitle(dto.getQtitle());
        qna.setQcontent(dto.getQcontent());
    	return qnaRepository.save(qna);
    }
    
    @Override
    public QnaDto view(Long qno, MemberDto member) throws BaCdException {
        return qnaRepository.findById(qno).filter(q -> "n".equals(q.getDelYn())) // 삭제되지 않은 것만
        		.orElseThrow(() -> new BaCdException(ErrorCode.PAGE_EMPTY, "존재하지 않는 문의글입니다."));
    }

    @Override
    public QnaDto answerQna(Long qno, String answerContent) {
        QnaDto qna = qnaRepository.findById(qno).filter(q -> "n".equals(q.getDelYn())) // 삭제되지 않은 것만
        		.orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "문의글을 찾을 수 없습니다."));
        qna.setAnswerContent(answerContent);
        qna.setStatus("complete");
        return qna;
    }

    @Transactional
    @Override
    public void deleteQna(Long qno) {
        QnaDto qna = qnaRepository.findById(qno).filter(q -> "n".equals(q.getDelYn())) // 삭제되지 않은 것만
        		.orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "문의글을 찾을 수 없습니다."));
        if ("답변완료".equals(qna.getStatus())) {
            throw new BaCdException(ErrorCode.COMPLETE_STATUS, "답변 완료된 문의는 삭제하실 수 없습니다.");
        }
        qna.setDelYn("y");
		qnaRepository.save(qna);
    }
}
