package com.project.app.audition.service;

import com.project.app.audition.dto.SupportMember;
import com.project.app.audition.dto.SupportOrderDto;
import com.project.app.audition.dto.SupportProjectDto;
import com.project.app.audition.repository.SupportMemberRepository;
import com.project.app.audition.repository.SupportOrderRepository;
import com.project.app.audition.repository.SupportProjectRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor // 레포지토리 자동 주입 (Lombok)
public class SupportServiceImpl implements SupportService {

    private final SupportProjectRepository projectRepository;
    private final SupportMemberRepository memberRepository;
    private final SupportOrderRepository supportOrderRepository;

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
    
    
//    ---------------------------------------------------------------카카오페이
    
    @Override
    @Transactional
    public Map<String, Object> readyPayment(SupportOrderDto orderDto) {
        // 1. 주문 번호 생성
        String uniqueOrderId = "ORD-" + System.currentTimeMillis();
        orderDto.setPartnerOrderId(uniqueOrderId);

        // 2. [수정] 가짜 데이터 대신 진짜 카카오 API 호출 함수를 여기서 실행!
        Map<String, Object> kakaoResponse = getKakaoPayUrl(orderDto);
        
        // 3. 카카오가 준 응답에서 TID 추출 및 저장
        if (kakaoResponse != null && kakaoResponse.containsKey("tid")) {
            orderDto.setTid((String) kakaoResponse.get("tid"));
            orderDto.setStatus("READY");
            supportOrderRepository.save(orderDto);
            return kakaoResponse; // 카카오가 준 데이터(결제 URL 포함)를 리액트로 보냄
        } else {
            throw new RuntimeException("카카오페이 결제 준비 중 오류 발생");
        }
    }

    public Map<String, Object> getKakaoPayUrl(SupportOrderDto orderDto) {
        // 1. 헤더 설정 [수정] SECRET_KEY와 키 사이의 띄어쓰기 및 형식 확인
        HttpHeaders headers = new HttpHeaders();
        // 실제 키 값 앞에 "SECRET_KEY "가 붙어야 합니다. (대소문자 주의)
        headers.set("Authorization", "SECRET_KEY DEV5AF16E011E90E89F908638FCB0AE957934741"); 
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. 파라미터 작성
        Map<String, String> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("partner_order_id", orderDto.getPartnerOrderId());
        params.put("partner_user_id", orderDto.getNickname());
        params.put("item_name", "연습생 데뷔 지원 후원");
        params.put("quantity", "1");
        params.put("total_amount", String.valueOf(orderDto.getAmount()));
        params.put("tax_free_amount", "0");
        params.put("approval_url", "http://localhost:3000/support/success");
        params.put("cancel_url", "http://localhost:3000/support/cancel");
        params.put("fail_url", "http://localhost:3000/support/fail");

        // 3. API 호출
        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        
        String url = "https://open-api.kakaopay.com/online/v1/payment/ready";
        
        try {
            return restTemplate.postForObject(url, request, Map.class);
        } catch (Exception e) {
            System.err.println("카카오 API 호출 에러: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> approvePayment(String pgToken, String tid) {
        SupportOrderDto order = supportOrderRepository.findByTid(tid)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));

        saveSupport(
            order.getSupportProject().getSupportId(), 
            order.getNickname(), 
            order.getAmount()
        );

        order.setStatus("PAID");
        return new HashMap<>(); 
    }
}