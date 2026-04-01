package com.project.app.board.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.service.MemberService;
import com.project.app.board.dto.BoardDto;
import com.project.app.board.service.BoardService;
import com.project.app.common.AjaxResponse;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/board")
public class BoardController {
	@Autowired
	BoardService boardService;

	@Autowired
	MemberService memberService;

	@Autowired
	HttpSession session;

	@ResponseBody
	@GetMapping("/list")
	public AjaxResponse list(
			@RequestParam(name="page", required=false, defaultValue="1") int page,
			@RequestParam(name="size", required=false, defaultValue="10") int size,
			@RequestParam(name="category", required=false, defaultValue="") String category,
			@RequestParam(name="search", required=false, defaultValue="") String search,
			@RequestParam(name="sortDir", defaultValue="DESC") String sortDir, Pageable pageable,
			Model model) {
		Map<String, Object> map = boardService.findAll(page, size, category, search, sortDir);	//service에서 정렬
		return AjaxResponse.success(map);
	}

	@ResponseBody
	@PostMapping("/save")
	public AjaxResponse save(BoardDto bdto, Model model) {
		MemberDto memberDto = Common.idCheck(session);
		if(bdto.getBtitle().equals("")) {
			throw new BaCdException(ErrorCode.INPUT_EMPTY, "제목을 입력해주세요.");
		}
		else if(bdto.getBcontent().equals("")) {
			throw new BaCdException(ErrorCode.INPUT_EMPTY, "내용을 입력해주세요.");
		}
		bdto.setMember(memberDto);
		boardService.save(bdto);
		return AjaxResponse.success();
	}

	/**
	 * 상세페이지 조회수+1
	 * @param bno
	 * @param category
	 * @param search
	 * @param model
	 * @return
	 */
	@ResponseBody
	@GetMapping("/view")
	public AjaxResponse view(@RequestParam(name="bno", required=true) Long bno,
			@RequestParam(name="category", required=false, defaultValue="") String category,
			@RequestParam(name="search", required=false, defaultValue="") String search,
			Model model) {
		MemberDto memberDto = (MemberDto) session.getAttribute("user");
		BoardDto bdto = BoardDto.builder().bno(bno).build();
		Map<String, Object> map = boardService.view(bdto, memberDto);
		map.put("category", category);
		map.put("search", search);
		model.addAttribute("map", map);
		return AjaxResponse.success(model);
	}

	/**
	 * 상세조회만
	 * @param bno
	 * @param category
	 * @param search
	 * @param model
	 * @return
	 */
	@ResponseBody
	@GetMapping("/detail")
	public AjaxResponse detail(@RequestParam(name="bno", required=true) Long bno,
			@RequestParam(name="category", required=false, defaultValue="") String category,
			@RequestParam(name="search", required=false, defaultValue="") String search,
			Model model) {
		BoardDto bdto = BoardDto.builder().bno(bno).build();
		BoardDto Board = boardService.findById(bdto);
		model.addAttribute("board", Board);
		return AjaxResponse.success(model);
	}

	@ResponseBody
	@PostMapping("/delete")
	public AjaxResponse delete(BoardDto bdto, Model model) {
		MemberDto memberDto = Common.idCheck(session);
		//BoardDto board = boardService.findById(bdto);
		//String fileName = board.getBfile();
		boardService.delete(bdto, memberDto);
		/*if (board != null && board.getBfile() != null) {
	        String fileUploadUrl = "c:/upload/";
	        File file = new File(fileUploadUrl + fileName);

	        // 2. 물리적 파일 존재 여부 확인 후 삭제
	        if (file.exists()) {
	            if (file.delete()) {
	                System.out.println("파일 삭제 성공: " + fileName);
	            } else {
	                System.out.println("파일 삭제 실패");
	            }
	        }
	    }*/
		return AjaxResponse.success();
	}

	@ResponseBody
	@PostMapping("/update")
	public AjaxResponse update(BoardDto bdto,
			//@RequestPart("file") MultipartFile file,
			Model model) {
		MemberDto memberDto = Common.idCheck(session);
		bdto.setMember(memberDto);
		if(bdto.getBtitle().equals("")) {
			throw new BaCdException(ErrorCode.INPUT_EMPTY, "제목을 입력해주세요.");
		}
		else if(bdto.getBcontent().equals("")) {
			throw new BaCdException(ErrorCode.INPUT_EMPTY, "내용을 입력해주세요.");
		}
		/*if(!file.isEmpty()) {
			String fName = file.getOriginalFilename();
			long time = System.currentTimeMillis();
			String refName = String.format("%s_%s", time, fName);
			String fileUploadUrl = "c:/upload/";
			try {
				File oldBfile = new File(fileUploadUrl + bdto.getBfile());	//새로운 파일있으면 기존은 삭제
				if (oldBfile.exists()) {
		            if (oldBfile.delete()) {
		                System.out.println("파일 삭제 성공: " + bdto.getBfile());
		            } else {
		                System.out.println("파일 삭제 실패");
		            }
		        }
				File f = new java.io.File(fileUploadUrl+refName);
				file.transferTo(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			bdto.setBfile(refName);
		}*/
		boardService.update(bdto, memberDto);
		return AjaxResponse.success();
	}

	/*@GetMapping("/breply")
	public String breply(BoardDto bdto, Model model) {
		BoardDto board = boardService.findById(bdto);
		model.addAttribute("board", board);
		return "board/breply";
	}

	@PostMapping("/breply")
	public String breply(Board bdto,
			@RequestPart("file") MultipartFile file,
			Model model) {
		String id=(String) session.getAttribute("session_id");
		MemberDto member = memberService.findById(id);
		bdto.setMember(member);
		if(!file.isEmpty()) {
			String fName = file.getOriginalFilename();
			long time = System.currentTimeMillis();
			String refName = String.format("%s_%s", time, fName);
			String fileUploadUrl = "c:/upload/";
			try {
				File f = new java.io.File(fileUploadUrl+refName);
				file.transferTo(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			bdto.setBfile(refName);
		}
		boardService.reply(bdto);
		return "redirect:/board/blist";
	}*/
}
