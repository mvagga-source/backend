package com.project.app.audition.service;

import com.project.app.audition.dto.IdolGuestbookDto;
import java.util.List;

public interface GuestbookService {
    // 방명록 저장
    IdolGuestbookDto saveMessage(IdolGuestbookDto dto);
    
    // 특정 아이돌 방명록 목록 가져오기
    List<IdolGuestbookDto> getMessagesByProfileId(Long profileId);
}