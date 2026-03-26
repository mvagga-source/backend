package com.project.app.qna.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.common.exception.BaCdException;
import com.project.app.qna.dto.QnaDto;
import com.project.app.qna.repository.QnaRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class QnaServiceImpl implements QnaService {
    @Autowired
	private QnaRepository qnaRepository;

    @Override
    public QnaDto save(QnaDto dto) {
        dto.setStatus("waiting");
        dto.setDelYn("n");
        return qnaRepository.save(dto);
    }

    @Override
    public Map<String, Object> findAllByMemberId(String memberId, Long lastQno) throws BaCdException {
    	Slice<QnaDto> list = qnaRepository.findNextPage(memberId, lastQno, null);
    	Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list.getContent());
		map.put("page", list.getNumber());
		//map.put("maxPage", list.getTotalPages());
		//map.put("totalCount", list.getTotalElements());
		return map;
    }

    @Override
    public QnaDto answerQna(Long qno, String answerContent) {
        QnaDto qna = qnaRepository.findById(qno).orElseThrow(() -> new RuntimeException("문의글을 찾을 수 없습니다."));
        qna.setAnswerContent(answerContent);
        //qna.setAnsDt(new Timestamp(System.currentTimeMillis()));
        qna.setStatus("complete");
        return qna; // Dirty Checking으로 업데이트됨
    }

    @Override
    public void deleteQna(Long qno) {
        QnaDto qna = qnaRepository.findById(qno).orElseThrow(() -> new RuntimeException("문의글을 찾을 수 없습니다."));
        qna.setDelYn("y");
    }
}
