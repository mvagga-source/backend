package com.project.app.audition.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.audition.dto.SupportOrderDto;

public interface SupportOrderRepository extends JpaRepository<SupportOrderDto, Long> {
    // 결제 승인 시 tid로 주문 정보를 찾아야 하므로 추가
    Optional<SupportOrderDto> findByTid(String tid);

}
