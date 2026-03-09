package com.project.app.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.user.dto.LoginRequest;
import com.project.app.user.dto.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

	Optional<Member> findByIdAndPw(String id, String pw);
	
}
