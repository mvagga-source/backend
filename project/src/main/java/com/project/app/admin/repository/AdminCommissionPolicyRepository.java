package com.project.app.admin.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.app.commissionPolicy.dto.CommissionPolicyDto;
import com.project.app.goods.dto.GoodsDto;

public interface AdminCommissionPolicyRepository extends JpaRepository<CommissionPolicyDto, Long> {
	
}
