package com.project.app.audition.controller;

import com.project.app.audition.dto.IdolGuestbookDto;
import com.project.app.audition.service.GuestbookService; // 인터페이스 임포트
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/guestbook")
public class GuestbookController {

    @Autowired
    private GuestbookService service; // 인터페이스 주입

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody IdolGuestbookDto dto) {
        IdolGuestbookDto saved = service.saveMessage(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<List<IdolGuestbookDto>> getList(@PathVariable("profileId") Long profileId) { 
        // @PathVariable 안에 "profileId"라고 이름을 명시해주는 것이 핵심!
        List<IdolGuestbookDto> list = service.getMessagesByProfileId(profileId);
        return ResponseEntity.ok(list);
    }
}