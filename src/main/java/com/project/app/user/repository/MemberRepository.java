package com.project.app.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.MemberDto;

public interface MemberRepository extends JpaRepository<MemberDto, String> {

	Optional<MemberDto> findByIdAndPw(String id, String pw);
	
}
