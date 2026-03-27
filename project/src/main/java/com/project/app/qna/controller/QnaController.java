package com.project.app.qna.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.idea.dto.IdeaDto;
import com.project.app.idea.service.IdeaService;
import com.project.app.qna.dto.QnaDto;
import com.project.app.qna.service.QnaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/qna")
public class QnaController {

    @Autowired QnaService qnaService;
    @Autowired HttpSession session;

    // 내 문의 내역 리스트
    @ResponseBody
    @GetMapping("/list")
    public AjaxResponse list(@RequestParam(name="page", defaultValue = "0") int page
							,@RequestParam(name="size", defaultValue = "10") int size
							,@RequestParam(name="sortDir", defaultValue = "DESC") String sortDir
							,@RequestParam(name="search", defaultValue = "") String search
							,@RequestParam(name="category", defaultValue = "") String category
							,@RequestParam(name="lastQno", defaultValue = "0") Long lastQno) {
        MemberDto memberDto = Common.idCheck(session);
        Map<String, Object> map = qnaService.findAllByMemberId(memberDto.getId(), lastQno);
        return AjaxResponse.success(map);
    }
    
    // 문의 상세보기
    @ResponseBody
    @GetMapping("/view")
    public AjaxResponse view(@RequestParam(name="qno") Long qno) {
    	MemberDto memberDto = Common.idCheck(session); // 로그인 체크
    	return AjaxResponse.success(qnaService.view(qno, memberDto));
    }
    
    // 문의 등록
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(QnaDto dto) {
        MemberDto memberDto = Common.idCheck(session);
        
        if(dto.getQtitle() == null || dto.getQtitle().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "문의 제목을 입력해주세요.");
        }else if(dto.getQcontent() == null || dto.getQcontent().trim().isEmpty()) {
	        throw new BaCdException(ErrorCode.INPUT_EMPTY, "문의 상세내용을 입력해주세요.");
        }
        
        dto.setMember(memberDto);
        return AjaxResponse.success(qnaService.save(dto));
    }
    
    // 문의 수정
    @ResponseBody
    @PostMapping("/update")
    public AjaxResponse update(QnaDto dto) {
        MemberDto memberDto = Common.idCheck(session);
        
        if(dto.getQtitle() == null || dto.getQtitle().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "문의 제목을 입력해주세요.");
        }else if(dto.getQcontent() == null || dto.getQcontent().trim().isEmpty()) {
	        throw new BaCdException(ErrorCode.INPUT_EMPTY, "문의 상세내용을 입력해주세요.");
        }
        
        dto.setMember(memberDto);
        return AjaxResponse.success(qnaService.update(dto, memberDto));
    }
    
    // 문의 삭제
    @ResponseBody
    @PostMapping("/delete")
    public AjaxResponse delete(@RequestParam(name="qno") Long qno) {
        Common.idCheck(session);
        qnaService.deleteQna(qno);
        return AjaxResponse.success();
    }

    // [관리자용] 답변 등록
    @ResponseBody
    @PostMapping("/saveAnswer")
    public AjaxResponse answer(@RequestParam(name="qno") Long qno, 
                               @RequestParam(name="content") String content) {
        // 관리자 권한 체크 로직 추가 필요
        qnaService.answerQna(qno, content);
        return AjaxResponse.success();
    }
}