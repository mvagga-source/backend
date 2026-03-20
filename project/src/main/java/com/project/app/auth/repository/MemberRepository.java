package com.project.app.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.auth.dto.LoginRequest;
import com.project.app.auth.dto.MemberDto;

public interface MemberRepository extends JpaRepository<MemberDto, String> {

	Optional<MemberDto> findByIdAndPw(String id, String pw);

	Optional<MemberDto> findByNickname(String nickname);
	
}
