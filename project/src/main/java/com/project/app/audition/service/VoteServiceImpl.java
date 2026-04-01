package com.project.app.audition.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.audition.dto.AuditionDto;
import com.project.app.audition.dto.IdolDto;
import com.project.app.audition.dto.VoteDetailDto;
import com.project.app.audition.dto.VoteDto;
import com.project.app.audition.dto.VoteRequestDto;
import com.project.app.audition.repository.AuditionRepository;
import com.project.app.audition.repository.IdolRepository;
import com.project.app.audition.repository.VoteRepository;
import com.project.app.auth.dto.MemberDto;
import com.project.app.auth.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

	private final VoteRepository      voteRepository;
    private final IdolRepository      idolRepository;
    private final AuditionRepository  auditionRepository;
    private final MemberRepository    memberRepository;

    // ── 투표 제출 ──────────────────────────────────────
    @Override
    @Transactional  // vote + voteDetail 함께 저장, 실패 시 롤백
    public void submitVote(String memberId, VoteRequestDto request) {

        // 1) 회원 조회
        MemberDto member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 회원이에요."));

        // 2) 오디션 회차 조회
        AuditionDto audition = auditionRepository.findById(request.getAuditionId())
            .orElseThrow(() -> new RuntimeException("존재하지 않는 오디션이에요."));

        // 3) 오늘 이미 투표했는지 확인
        if (voteRepository.existsByMemberAndAuditionAndVoteDate(
                member, audition, LocalDate.now())) {
            throw new RuntimeException("오늘은 이미 투표했어요. 내일 다시 투표해주세요.");
        }

        // 4) 최대 투표 인원 초과 확인 (기본 7명)
        if (request.getIdolIds().size() > audition.getMaxVoteCount()) {
            throw new RuntimeException(
                "최대 " + audition.getMaxVoteCount() + "명까지 투표할 수 있어요."
            );
        }

        // 5) 중복 아이돌 선택 확인
        long distinctCount = request.getIdolIds().stream().distinct().count();
        if (distinctCount != request.getIdolIds().size()) {
            throw new RuntimeException("동일한 아이돌을 중복 선택할 수 없어요.");
        }

        // 6) vote 묶음 생성 (voteDate는 @PrePersist에서 자동 설정)
        VoteDto vote = VoteDto.builder()
            .member(member)
            .audition(audition)
            .build();

        // 7) 선택한 아이돌마다 voteDetail 생성
        for (Long idolId : request.getIdolIds()) {
            IdolDto idol = idolRepository.findById(idolId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이돌이에요."));

            // 탈락한 아이돌에게 투표 방지
            if (!"active".equals(idol.getStatus())) {
                throw new RuntimeException(idol.getIdolId() + "번 참가자는 탈락한 참가자예요.");
            }
            
            // IdolProfileDto 연결 후 교체
            // throw new RuntimeException(idol.getIdolProfile().getName() + "님은 탈락한 참가자예요.")

            VoteDetailDto detail = VoteDetailDto.builder()
                .vote(vote)
                .idol(idol)
                .build();

            vote.getVoteDetails().add(detail);
        }

        // 8) 저장 (CascadeType.ALL 로 voteDetail도 함께 저장)
        voteRepository.save(vote);
    }

    // ── 오늘 투표 여부 확인 ────────────────────────────
    @Override
    public boolean hasVotedToday(String memberId, Long auditionId) {

        MemberDto member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 회원이에요."));

        AuditionDto audition = auditionRepository.findById(auditionId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 오디션이에요."));

        return voteRepository.existsByMemberAndAuditionAndVoteDate(
            member, audition, LocalDate.now()
        );
    }
    
    // ── 아이돌 목록 조회 (내부 검증용) ────────────────
    private List<IdolDto> getActiveIdols(Long auditionId) {
        AuditionDto audition = auditionRepository.findById(auditionId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 오디션이에요."));
        return idolRepository.findByAuditionAndStatus(audition, "active");
    }
    
    // 오늘 투표한 아이돌 ID 목록
    @Override
    public List<Long> getVotedIdolIds(String memberId, Long auditionId) {

        MemberDto member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 회원이에요."));

        AuditionDto audition = auditionRepository.findById(auditionId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 오디션이에요."));

        return voteRepository.findVotedIdolIdsByMemberAndAuditionAndVoteDate(
            member.getId(), audition.getAuditionId(), LocalDate.now()
        );
    }

}
