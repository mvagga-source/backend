package com.project.app.notice.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.notice.dto.NoticeDto;
import com.project.app.notice.service.NoticeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private HttpSession session;

    /**
     * 공지사항 리스트 (전체 조회)
     */
    @ResponseBody
    @GetMapping("/list")
    public AjaxResponse list(@RequestParam(name="page", required=false, defaultValue="1") int page,
			@RequestParam(name="size", required=false, defaultValue="10") int size,
			@RequestParam(name="category", required=false, defaultValue="") String category,
			@RequestParam(name="search", required=false, defaultValue="") String search,
			Pageable pageable) {
        Map<String, Object> map = noticeService.list(page, size, category, search);
        return AjaxResponse.success(map);
    }

    /**
     * 메인화면용: 공지사항 최신글 5개 (기간 내 노출)
     */
    @ResponseBody
    @GetMapping("/mainList")
    public AjaxResponse mainList() {
        Map<String, Object> map = noticeService.getMainNotices();
        return AjaxResponse.success(map);
    }

    /**
     * 공지사항 상세 (조회수 증가 포함)
     */
    @ResponseBody
    @GetMapping("/view")
    public AjaxResponse view(@RequestParam(name="nno") Long nno) {
        NoticeDto notice = noticeService.findById(nno);
        
        if (notice == null || "y".equals(notice.getDelYn())) {
            throw new BaCdException(ErrorCode.NOT_FOUND, "존재하지 않거나 삭제된 공지사항입니다.");
        }

        // 조회수 증가 로직 (Service에서 처리하도록 권장하지만, 상세조회 시 setter 활용 가능)
        notice.setNhit(notice.getNhit() + 1);
        noticeService.save(notice); // 수정한 조회수 저장

        return AjaxResponse.success(notice);
    }

    /**
     * 공지사항 저장 (관리자 권한 체크 필요)
     */
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(NoticeDto ndto) {
        MemberDto memberDto = Common.idCheck(session);
        //admin인지 확인필요
        
        if (ndto.getNtitle() == null || ndto.getNtitle().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "공지 제목을 입력해주세요.");
        } else if(ndto.getNcontent() == null || ndto.getNcontent().isEmpty()) {
        	throw new BaCdException(ErrorCode.INPUT_EMPTY, "공지 내용을 입력해주세요.");
        }
        
        ndto.setMember(memberDto);
        // 기본값 설정 (필요시)
        if (ndto.getStartDate() == null) ndto.setStartDate(LocalDateTime.now());
        if (ndto.getEndDate() == null) ndto.setEndDate(LocalDateTime.now().plusDays(7));
        return AjaxResponse.success(noticeService.save(ndto));
    }

    /**
     * 공지사항 수정
     */
    @ResponseBody
    @PostMapping("/update")
    public AjaxResponse update(NoticeDto ndto) {
        Common.idCheck(session); // 관리자 권한 체크용
        if (ndto.getNno() == null) {
            throw new BaCdException(ErrorCode.INVALID_PARAMETER, "수정할 번호가 없습니다.");
        }
        return AjaxResponse.success(noticeService.update(ndto));
    }

    /**
     * 공지사항 삭제
     */
    @ResponseBody
    @PostMapping("/delete")
    public AjaxResponse delete(@RequestParam(name="nno") Long nno) {
        Common.idCheck(session);
        return AjaxResponse.success(noticeService.delete(nno));
    }
}