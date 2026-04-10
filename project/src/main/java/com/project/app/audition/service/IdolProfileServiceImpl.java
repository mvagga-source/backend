package com.project.app.audition.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.audition.dto.IdolMediaDto;
import com.project.app.audition.dto.IdolProfileDto;
import com.project.app.audition.dto.IdolResponseDto;
import com.project.app.audition.repository.IdolMediaRepository;
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
    
    @Autowired
    private IdolMediaRepository mediaRepository;

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
    
    // 하단 이미지
    @Override // 인터페이스(IdolProfileService)에도 이 메서드가 정의되어 있어야 합니다.
    public Map<String, Object> getIdolDetail(Long profileId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 아이돌 기본 정보 가져오기
        // ✅ 수정: 클래스명(대문자)이 아닌 주입받은 변수명(소문자) idolProfileRepository를 사용하세요.
        IdolProfileDto profile = idolProfileRepository.findById(profileId).orElse(null);
        
        // 2. 해당 아이돌의 미디어 리스트 가져오기
        List<IdolMediaDto> mediaList = mediaRepository.findByProfileProfileId(profileId);
        
        result.put("profile", profile);
        result.put("mediaList", mediaList); 
        
        return result;
    }

    
    //----------------------------------------------------관리자

    @Override
    public Map<String, Object> getProfileListForAdmin(int page) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 1. DB에서 리스트 조회 (Repository 변수명을 본인 프로젝트에 맞게 확인!)
            List<IdolProfileDto> list = idolProfileRepository.findAll(); 
            
            // 2. 데이터가 null이면 빈 리스트라도 생성 (NPE 방지)
            if (list == null) {
                list = new ArrayList<>();
            }

            // 3. Map에 담기 (JSP에서 쓰는 key값과 동일해야 함)
            resultMap.put("list", list);
            resultMap.put("totalCount", list.size());
            resultMap.put("page", page);
            resultMap.put("startPage", 1);
            resultMap.put("endPage", 1); 
            
            System.out.println("✅ 서비스에서 보낼 데이터 확인: " + list.size() + "명 조회됨");
            
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("list", new ArrayList<>());
            resultMap.put("totalCount", 0);
        }

        return resultMap; // 🔥 절대 null을 리턴하면 안 됩니다!
    }


	@Override
	public String upload(MultipartFile mainImgFile) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IdolProfileDto save(IdolProfileDto profileDto) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void saveMedia(IdolMediaDto media) {
		// TODO Auto-generated method stub
		
	} 
    
    
    
    
}