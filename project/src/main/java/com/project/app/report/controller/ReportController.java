package com.project.app.report.controller;

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
import org.springframework.web.multipart.MultipartFile;

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
    
    @ResponseBody
    @GetMapping("/list")
    public AjaxResponse list(@RequestParam(name="page", defaultValue = "0") int page
							,@RequestParam(name="size", defaultValue = "10") int size
							,@RequestParam(name="sortDir", defaultValue = "DESC") String sortDir
							,@RequestParam(name="search", defaultValue = "") String search
							,@RequestParam(name="category", defaultValue = "") String category
							,@RequestParam(name="lastRepono", defaultValue = "0") Long lastRepono) {
        //MemberDto memberDto = Common.idCheck(session);
        Map<String, Object> map = reportService.findAll(lastRepono);
        return AjaxResponse.success(map);
    }

    // 신고 접수
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(ReportDto dto,
    		@RequestParam(value = "file", required = false) MultipartFile file) throws BaCdException {
        MemberDto memberDto = Common.idCheck(session);

        if(dto.getReportType() == null || dto.getReportType().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "신고 유형을 입력해주세요.");			//방어코드
        } else if(dto.getTargetType() == null || dto.getTargetType().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "신고 대상 (URL 또는 사용자)를 입력해주세요.");
        } else if(dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "신고 사유를 입력해주세요.");
        } else if(dto.getIdol() == null || dto.getIdol().getProfileId() == null || dto.getIdol().getProfileId() <= 0) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "참가자를 입력해주세요.");
        } else if(dto.getReasonContent() == null || dto.getReasonContent().trim().isEmpty()) {
	        throw new BaCdException(ErrorCode.INPUT_EMPTY, "상세 설명을 입력해주세요.");
        }
        /*if(dto.getTargetId() == null) {
            throw new BaCdException(ErrorCode.INVALID_PARAMETER, "신고 대상 정보가 없습니다.");
        }*/

        dto.setMember(memberDto);
        return AjaxResponse.success(reportService.save(dto, file));
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