package com.project.app.admin.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.app.admin.service.AdminCommissionPolicyService;
import com.project.app.admin.service.AdminGoodsService;
import com.project.app.commissionPolicy.dto.CommissionPolicyDto;
import com.project.app.common.AjaxResponse;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/commission")
@RequiredArgsConstructor
public class AdminCommissionPolicyController {
	private final AdminCommissionPolicyService adminCommissionPolicy;

    // 조회 (AJAX GET)
	@ResponseBody
    @GetMapping("/policy")
    public AjaxResponse getPolicy() {
        return AjaxResponse.success(adminCommissionPolicy.getPolicy());
    }

    // 수정 (AJAX POST)
	@ResponseBody
    @PostMapping("/policy")
    public AjaxResponse updatePolicy(@RequestBody CommissionPolicyDto dto) {
        return AjaxResponse.success(adminCommissionPolicy.updatePolicy(dto));
    }
}
