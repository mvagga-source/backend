package com.project.app.report.controller;

import java.util.List;

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
import com.project.app.report.dto.ReportDto;
import com.project.app.report.service.ReportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/report")
public class ReportController {

    @Autowired ReportService reportService;
    @Autowired HttpSession session;

    // 신고 접수
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(@RequestBody ReportDto dto) throws BaCdException {
        MemberDto memberDto = Common.idCheck(session);

        if(dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "신고 사유를 입력해주세요.");
        }
        if(dto.getTargetId() == null) {
            throw new BaCdException(ErrorCode.INVALID_PARAMETER, "신고 대상 정보가 없습니다.");
        }

        dto.setMember(memberDto);
        reportService.save(dto);
        return AjaxResponse.success();
    }

    // 관리자용 신고 상태 변경
    @ResponseBody
    @PostMapping("/update")
    public AjaxResponse updateStatus(@RequestParam(name="rno") Long rno, 
                                     @RequestParam(name="status") String status) {
    	MemberDto memberDto = Common.idCheck(session);
        reportService.update(rno, status);
        return AjaxResponse.success();
    }
}