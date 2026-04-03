package com.project.app.notificationSetting.service;

import java.util.Map;

import com.project.app.common.exception.BaCdException;
import com.project.app.notificationSetting.dto.NotificationSettingDto;

public interface NotificationSettingService {

	/**
	 * [설정 조회]
	 * 사용자의 알림 설정 정보를 가져옴
	 * 만약 DB에 설정 정보가 없다면(첫 가입 후 첫 조회 시) 기본값(모두 'y')으로 생성하여 반환
	 */
	NotificationSettingDto getSetting(String memberId) throws BaCdException;

	/**
	 * [설정 업데이트]
	 * 사용자가 토글 버튼을 클릭했을 때 특정 항목만 업데이트
	 * @param memberId 사용자 ID
	 * @param updates 변경할 필드와 값 (예: {"allowBoardLike": "n"})
	 */
	NotificationSettingDto updateSetting(String memberId, Map<String, Object> updates) throws BaCdException;

}
