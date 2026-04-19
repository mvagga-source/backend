package com.project.app.admin.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.admin.repository.AdminCommissionPolicyRepository;
import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminGoodsReturnRepository;
import com.project.app.commissionPolicy.dto.CommissionPolicyDto;
import com.project.app.common.exception.BaCdException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminCommissionPolicyServiceImpl implements AdminCommissionPolicyService {
	private final AdminCommissionPolicyRepository adminCommissionPolicy;

    // 조회
    public CommissionPolicyDto getPolicy() throws BaCdException {
        return adminCommissionPolicy.findById(1L)
                .orElseThrow(() -> new RuntimeException("정산 정책이 없습니다."));
    }

    // 수정
    @Transactional
    public CommissionPolicyDto updatePolicy(CommissionPolicyDto dto) throws BaCdException {

        CommissionPolicyDto entity = adminCommissionPolicy.findById(1L)
                .orElseThrow(() -> new RuntimeException("정산 정책이 없습니다."));

        entity.setPgFeeRate(dto.getPgFeeRate());
        entity.setPlatformFeeRate(dto.getPlatformFeeRate());
        entity.setTaxRate(dto.getTaxRate());

        return adminCommissionPolicy.save(entity);
    }
}
