package com.project.app.audition.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.app.audition.dto.IdolResponseDto;
import com.project.app.audition.repository.IdolRepository;

@Service
public class IdolProfileServiceImpl implements IdolProfileService {

    // 에러 1 해결: final을 붙였다면 생성자나 초기화가 필요한데, 
    // 가장 쉬운 방법은 @Autowired를 쓰고 final을 빼는 것입니다.
    @Autowired
    private IdolRepository idolRepository; 

    // 에러 2 해결: 인터페이스(IdolProfileService)에 정의된 
    // 메서드 이름, 파라미터 타입(Long, Long)과 완벽히 일치시켜야 합니다.
    @Override
    public IdolResponseDto findIdolWithVote(Long auditionId, Long idolProfileId) {
        // 리포지토리 객체의 메서드를 호출해서 리턴!
        return idolRepository.findIdolWithVote(auditionId, idolProfileId);
    }
}