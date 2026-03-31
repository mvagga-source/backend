package com.project.app.audition.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.dto.IdolResponseDto;
import com.project.app.audition.repository.IdolProfileRepository;
import com.project.app.audition.repository.IdolRepository;
import com.project.app.common.exception.BaCdException;

import jakarta.transaction.Transactional;

@Service
public class IdolProfileServiceImpl implements IdolProfileService {

    // 에러 1 해결: final을 붙였다면 생성자나 초기화가 필요한데, 
    // 가장 쉬운 방법은 @Autowired를 쓰고 final을 빼는 것입니다.
    @Autowired
    private IdolRepository idolRepository; 
    
    @Autowired
    private IdolProfileRepository idolProfileRepository;

    // 에러 2 해결: 인터페이스(IdolProfileService)에 정의된 
    // 메서드 이름, 파라미터 타입(Long, Long)과 완벽히 일치시켜야 합니다.
    @Override
    public IdolResponseDto findIdolWithVote(Long auditionId, Long idolProfileId) {
        // 리포지토리 객체의 메서드를 호출해서 리턴!
        return idolRepository.findIdolWithVote(auditionId, idolProfileId);
    }

    
    // 프로필 가져오기
	@Override
	public IdolProfileDto findById(Long idolProfileId) {
		IdolProfileDto idolProfileDto = idolProfileRepository.findById(idolProfileId).orElse(null);
		return idolProfileDto;
	}
	
	// ✅ 추가: 이미지 파일명을 DB에 반영하는 로직
    @Override
    @Transactional // 데이터 수정을 위해 필요합니다.
    public void updateMainImgUrl(Long idolProfileId, String fileName) {
        // 엔티티를 찾아서 파일명만 슥 바꿔주면 끝!
        idolProfileRepository.findById(idolProfileId).ifPresent(profile -> {
            profile.setMainImgUrl(fileName);
        });
    }
    
    public List<IdolProfileDto> findAll() throws BaCdException {
    	return idolProfileRepository.findAll();
    }
}