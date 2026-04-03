package com.project.app.notificationSetting.service;

import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.repository.MemberRepository;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.notificationSetting.dto.NotificationSettingDto;
import com.project.app.notificationSetting.repository.NotificationSettingRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class NotificationSettingServiceImpl implements NotificationSettingService {

    private final NotificationSettingRepository settingRepository;
    
    private final MemberRepository memberRepository;

    /**
     * [설정 조회]
     * 사용자의 알림 설정 정보를 가져옴
     * 만약 DB에 설정 정보가 없다면(첫 가입 후 첫 조회 시) 기본값(모두 'y')으로 생성하여 반환
     */
    @Override
    @Transactional
    public NotificationSettingDto getSetting(String memberId) throws BaCdException {
    	// 1. 조회 시도
        return settingRepository.findById(memberId)
            .orElseGet(() -> {
            // 2. 멤버 조회 (영속 상태의 Entity여야 함)
            MemberDto member = memberRepository.findById(memberId).orElse(null);

			if (member != null) {
				// 3. DB에 세팅이 없으면 기본값으로 생성(회원가입시 같이 생성해주면 좋지만 안 되어 있을 경우)
				NotificationSettingDto defaultSetting = NotificationSettingDto.builder()
						.member(member) // @MapsId가 이 member의 ID를 PK로 사용함
						.allowBoardComment("y")
						.allowBoardLike("y")
						.allowGoodsReview("y")
						.allowGoodsReviewLike("y")
						.allowGoodsTrade("y")
						.allowQnaAnswer("y")
						.build();
				
				// 4. 명시적으로 save 후 결과 반환
				return settingRepository.save(defaultSetting);
			}
			return null;
         });
    }

    /**
     * [설정 업데이트]
     * 사용자가 토글 버튼을 클릭했을 때 특정 항목만 업데이트
     * @param memberId 사용자 ID
     * @param updates 변경할 필드와 값 (예: {"allowBoardLike": "n"})
     */
    @Override
    @Transactional
    public NotificationSettingDto updateSetting(String memberId, Map<String, Object> updates) throws BaCdException {
        // 기존 설정 조회 (없으면 위 메서드를 통해 생성됨)
        NotificationSettingDto setting = getSetting(memberId);

        // 넘어온 값에 따라 필드 업데이트
        if (updates.containsKey("allowBoardComment")) {
            setting.setAllowBoardComment((String) updates.get("allowBoardComment"));
        }
        if (updates.containsKey("allowBoardLike")) {
            setting.setAllowBoardLike((String) updates.get("allowBoardLike"));
        }
        if (updates.containsKey("allowGoodsReview")) {
            setting.setAllowGoodsReview((String) updates.get("allowGoodsReview"));
        }
        if (updates.containsKey("allowGoodsReviewLike")) {
        	setting.setAllowGoodsReviewLike((String) updates.get("allowGoodsReviewLike"));
        }
        if (updates.containsKey("allowGoodsTrade")) {
            setting.setAllowGoodsTrade((String) updates.get("allowGoodsTrade"));
        }
        if (updates.containsKey("allowQnaAnswer")) {
            setting.setAllowQnaAnswer((String) updates.get("allowQnaAnswer"));
        }

        // 별도로 save()를 호출하지 않아도 @Transactional에 의해 변경사항이 DB에 반영
        return setting;
    }
}