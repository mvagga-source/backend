package com.project.app.idea.controller;

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
import com.project.app.idea.dto.IdeaDto;
import com.project.app.idea.service.IdeaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/idea")
public class IdeaController {

    @Autowired IdeaService ideaService;
    
    @Autowired HttpSession session;

    // 아이디어 목록 조회 (더보기용 Slice)
    @ResponseBody
    @GetMapping("/list")
    public AjaxResponse list(@RequestParam(name="page", defaultValue = "0") int page
			,@RequestParam(name="size", defaultValue = "10") int size
			,@RequestParam(name="sortDir", defaultValue = "DESC") String sortDir
			,@RequestParam(name="search", defaultValue = "") String search
			,@RequestParam(name="category", defaultValue = "") String category
			,@RequestParam(name="lastRepono", defaultValue = "0") Long lastIdeano) {
        Map<String, Object> map = ideaService.findAll(lastIdeano);
        return AjaxResponse.success(map);
    }

    // 아이디어 등록
    @ResponseBody
    @PostMapping("/save")
    public AjaxResponse save(IdeaDto dto,
    		@RequestParam(value = "file", required = false) MultipartFile file) {
        MemberDto memberDto = Common.idCheck(session);
        
        if(dto.getIdeacategory() == null || dto.getIdeacategory().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "카테고리를 입력해주세요.");
        }
        if(dto.getIdeatitle() == null || dto.getIdeatitle().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "제안 제목을 입력해주세요.");
        }
        else if(dto.getIdeacontent() == null || dto.getIdeacontent().trim().isEmpty()) {
            throw new BaCdException(ErrorCode.INPUT_EMPTY, "상세 내용을 입력해주세요.");
        }

        dto.setMember(memberDto);
        return AjaxResponse.success(ideaService.save(dto, file));
    }

    // 내 아이디어 조회
    @ResponseBody
    @GetMapping("/view")
    public AjaxResponse myIdeas() {
        MemberDto memberDto = Common.idCheck(session);
        List<IdeaDto> list = ideaService.findAllByMemberId(memberDto.getId());
        return AjaxResponse.success(list);
    }
}