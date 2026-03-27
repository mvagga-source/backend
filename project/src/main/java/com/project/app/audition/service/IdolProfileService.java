package com.project.app.audition.service;

import com.project.app.audition.dto.IdolResponseDto;

public interface IdolProfileService {
    // 딱 이 한 줄만 있으면 됩니다. (세미콜론 ; 으로 끝내기)
    IdolResponseDto findIdolWithVote(Long auditionId, Long idolProfileId);
}