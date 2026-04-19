package com.project.app.admin.service;

import com.project.app.commissionPolicy.dto.CommissionPolicyDto;
import com.project.app.common.exception.BaCdException;

public interface AdminCommissionPolicyService {
	public CommissionPolicyDto getPolicy() throws BaCdException;
	
	public CommissionPolicyDto updatePolicy(CommissionPolicyDto dto) throws BaCdException;
}
