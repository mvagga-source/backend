package com.project.app.audition.service;

import com.project.app.audition.dto.IdolGuestbookDto;
import com.project.app.audition.repository.IdolGuestbookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GuestbookServiceImpl implements GuestbookService {

    @Autowired
    private IdolGuestbookRepository repository;

    // 필터링할 욕설 리스트
    private final String[] forbiddenWords = {"욕설1", "욕설2", "나쁜말"};

    @Override
    public IdolGuestbookDto saveMessage(IdolGuestbookDto dto) {
        // 1. 욕설 필터링 로직
        String content = dto.getContent();
        for (String word : forbiddenWords) {
            content = content.replace(word, "***");
        }
        dto.setContent(content); // 필터링된 내용으로 다시 세팅

        // 2. DB 저장 후 결과 반환
        return repository.save(dto);
    }

    @Override
    public List<IdolGuestbookDto> getMessagesByProfileId(Long profileId) {
        // Repository를 통해 DB에서 데이터를 꺼내옴
        return repository.findByProfileIdOrderByIdDesc(profileId);
    }
}