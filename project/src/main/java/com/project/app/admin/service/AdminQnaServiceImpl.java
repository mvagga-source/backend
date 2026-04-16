package com.project.app.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminGoodsRepository;
import com.project.app.admin.repository.AdminGoodsReviewRepository;
import com.project.app.admin.repository.AdminQnaRepository;
import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.service.MemberService;
import com.project.app.common.Common;
import com.project.app.common.GridUtils;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.notification.dto.NotificationDto;
import com.project.app.qna.dto.QnaDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminQnaServiceImpl implements AdminQnaService {
	private final AdminQnaRepository adminQnaRepository;
	
	private final MemberService memberService;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> ajaxList(int page, int perPage, String category, String search, 
                                      String status, String startDate, String endDate) {
        Pageable pageable = PageRequest.of(page - 1, perPage);

        // 등록일 검색 조건 처리
        LocalDateTime startDt = null;
        LocalDateTime endDt = null;
        if (startDate != null && !startDate.isEmpty()) {
	    	startDt = Common.parseFlexibleDate(startDate, true);
	    }
	    if (endDate != null && !endDate.isEmpty()) {
	    	endDt = Common.parseFlexibleDate(endDate, false);
	    }
	    
	    // 시작일 > 종료일 체크
	    if (startDt != null && endDt != null) {
	    	if (startDt.isAfter(endDt)) {
	            throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "시작일은 종료일보다 클 수 없습니다.");
	        }
	    }

        // Repository에서 QueryDSL 등을 사용해 검색 수행 (상태값 status 추가)
        Page<QnaDto> resultPage = adminQnaRepository.findAllQnaWithFilter(
                category, search, status, startDt, endDt, pageable
        );
        Map<String, Object> map = GridUtils.gridRes(resultPage, perPage);
        return map;
    }
    
    @Override
	public QnaDto view(Long qno) throws BaCdException {
		return adminQnaRepository.findById(qno).orElseThrow(() -> new BaCdException(ErrorCode.PAGE_EMPTY, "해당 문의글이 존재하지 않습니다."));
	}

    @Override
    public Map<String, Object> saveReply(Map<String, Object> param) {
        Long qno = Long.valueOf(param.get("qno").toString());
        String answerContent = (String) param.get("answerContent");
        //String status = (String) param.get("status");

        // 기존 데이터 조회 후 답변 정보만 업데이트
        QnaDto qna = adminQnaRepository.findById(qno)
                .orElseThrow(() -> new RuntimeException("해당 문의글이 존재하지 않습니다."));

        qna.setAnswerContent(answerContent);		//문의답변내용
        qna.setStatus("답변완료");						//문의답변완료 상태

        adminQnaRepository.save(qna);
        
		MemberDto admin = memberService.findById(new MemberDto().builder().id("admin").build());
        
        //문의후 사용자에게 답변 상태 알림 전송
        NotificationDto sellerEvent = NotificationDto.builder()
                .member(qna.getMember())
                .sender(admin)		//관리자아이디
                .nocontent(qna.getMember().getNickname()+"님, 문의하신 내용에 답변이 등록되었습니다.")
                .type("QNA_ANSWER") // 설정에서 allowQnaAnswer로 체크됨
                .url("/Community/QnaView/" + qna.getQno()) // 문의글 상세 페이지
                .isRead("n")
                .build();
        eventPublisher.publishEvent(sellerEvent);

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        return result;
    }

    @Override
    public Map<String, Object> deleteAll(Map<String, Object> param) {
        List<Map<String, Object>> rows = (List<Map<String, Object>>) param.get("deletedRows");
        if (rows != null) {
            rows.forEach(row -> {
                Long qno = Long.valueOf(row.get("qno").toString());
                adminQnaRepository.deleteById(qno);
            });
        }
        Map<String, Object> map = new HashMap<>();
        map.put("result", true);
        map.put("resultCnt", rows != null ? rows.size() : 0);
        return map;
    }

}
