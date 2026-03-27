package com.project.app.audition.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.audition.service.IdolProfileService;

@Controller
@RequestMapping("/api/idolProfile")
public class IdolProfileController {
	@Autowired
	IdolProfileService idolProfileService;
	
	@ResponseBody
	@GetMapping("/idolViewVote")
	public Map<String, Object> idolprofileVote(@RequestParam(name="auditionId", required = false) Long auditionId, 
			@RequestParam(name="idolProfileId", required = false) Long idolProfileId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("idolProfile", idolProfileService.findIdolWithVote(auditionId, idolProfileId));
		return map;
	}
}
