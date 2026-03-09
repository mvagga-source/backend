package com.project.app.auth.service;


//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import com.project.app.security.handler.JwtProvider;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final JwtProvider jwtProvider;
//
//    public LoginResponse login(String username, String password) {
//        // DB 체크 대신 샘플
//        if (!username.equals("admin") || !password.equals("1234")) {
//            throw new RuntimeException("로그인 실패");
//        }
//
//        String token = jwtProvider.createToken(username, "ADMIN");
//        return new LoginResponse(token);
//    }
//}
