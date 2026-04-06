package com.project.app.audition.service;

import com.project.app.audition.dto.SupportMember;
import com.project.app.audition.dto.SupportProjectDto;
import com.project.app.audition.repository.SupportMemberRepository;
import com.project.app.audition.repository.SupportProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // 레포지토리 자동 주입 (Lombok)
public class SupportServiceImpl implements SupportService {

    private final SupportProjectRepository projectRepository;
    private final SupportMemberRepository memberRepository;

    @Override
    @Transactional // 데이터 일관성을 위해 필수!
    public void saveSupport(Long supportId, String nickname, Long amount) {
        // 1. 해당 광고 프로젝트 찾기
        SupportProjectDto project = projectRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("해당 서포트 프로젝트를 찾을 수 없습니다."));

        // 2. 후원 로그(SupportMember) 생성 및 저장
        SupportMember newMember = SupportMember.builder()
                .supportProject(project)
                .nickname(nickname)
                .amount(amount)
                .build();
        memberRepository.save(newMember);

        // 3. 프로젝트 정보 업데이트 (현재 금액 합산 및 참여자 수 증가)
        // 기존 금액이 null일 경우를 대비해 처리 루틴을 넣으면 더 안전합니다.
        Long currentPrice = (project.getCurrPrice() == null) ? 0L : project.getCurrPrice();
        Integer currentParticipants = (project.getParticipants() == null) ? 0 : project.getParticipants();

        project.setCurrPrice(currentPrice + amount);
        project.setParticipants(currentParticipants + 1);

        // JPA의 Dirty Checking 덕분에 따로 projectRepository.save(project)를 안 해도 
        // 메서드가 끝날 때 자동으로 DB에 업데이트됩니다.
    }

    @Override
    public List<SupportMember> getLogsByProjectId(Long supportId) {
        // 특정 프로젝트 ID에 해당하는 로그들을 최신순으로 가져오는 로직
        return memberRepository.findBySupportProject_SupportIdOrderByCreatedAtDesc(supportId);
    }
    
    @Override
    public SupportProjectDto getProjectById(Long supportId) {
    	System.out.println("### 파라미터로 넘어온 ID: " + supportId);
        // 1. DB에서 프로젝트 정보를 가져옵니다.
        return projectRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("해당 프로젝트를 찾을 수 없습니다."));
    }
}