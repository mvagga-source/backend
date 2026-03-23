package com.project.app.goodsreview.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsreview.dto.GoodsReviewDto;
import com.project.app.goodsreview.repository.GoodsReviewRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsReviewServiceImpl implements GoodsReviewService {
	@Autowired
    private GoodsReviewRepository goodsReviewRepository;
	
	// 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findAll(Long gno, int size, Long lastGrno) throws BaCdException {
    	// 기준이 되는 그룹 10개 추출
    	/*Pageable pageable = PageRequest.of(0, size);
        List<GoodsReviewDto> list = goodsReviewRepository.findAllReviews(gno, lastGrno, pageable);

        
        // 변환 시 'y'인 경우 내용 숨기기(보안 문제로 미리 안보이게 세팅도 추가)X
        list.forEach(this::processDeletedContent);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.put("lastGrno", list.isEmpty() ? 0 : list.get(list.size() - 1).getGrno());
        map.put("isLast", list.size() < size);
        return map;*/
        
        // 정상적인(삭제되지 않은) 리뷰 ID를 size만큼 가져옴
        List<Long> validIds = goodsReviewRepository.findValidRootIds(gno, lastGrno, size);
        
        if (validIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 해당 ID들에 속한 리뷰들과 그 자식(답글)들을 모두 가져옴
        // 이때 삭제된 답글들도 함께 불러와집니다.
        List<GoodsReviewDto> list = goodsReviewRepository.findAllByIds(validIds);

        // 3. 응답 구성
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("lastGrno", validIds.get(validIds.size() - 1)); // 기준이 된 마지막 '정상' ID
        result.put("isLast", validIds.size() < size);
        
        return result;
    }
    
    // 삭제된 리뷰의 내용을 가공하는 메소드
    private void processDeletedContent(GoodsReviewDto review) {
        if ("y".equals(review.getDelYn())) {
            review.setGrcontents("삭제된 리뷰입니다.");
            review.setMember(null); 
        }
        // 자식 답글들도 재귀적으로 처리
        /*if (review.getChildren() != null) {
            review.getChildren().forEach(this::processDeletedContent);
        }*/
    }

    // 리뷰 등록
	@Transactional
    @Override
    public GoodsReviewDto save(GoodsReviewDto dto, MultipartFile file, MemberDto member) throws BaCdException {
        // 이미지 처리 로직 (파일 저장 서비스가 별도로 있다면 호출)
        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file);
            dto.setGrImg(filePath);
            dto.setIsPhoto("y");
        }

        dto.setMember(member);
        dto.setDelYn("n"); // 초기값 세팅

        return goodsReviewRepository.save(dto);
    }

    // 리뷰 수정
	@Transactional
    @Override
    public GoodsReviewDto update(GoodsReviewDto dto, MultipartFile file, MemberDto member) throws BaCdException {
        GoodsReviewDto review = goodsReviewRepository.findById(dto.getGrno()).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "존재하지 않는 리뷰입니다."));

        // 작성자가 맞는지 확인
        if (!review.getMember().getId().equals(member.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);
        }

        // 내용 및 별점 업데이트
        review.setGrcontents(dto.getGrcontents());
        review.setRating(dto.getRating());

        // 새 이미지 첨부 시 업데이트
        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file);
            review.setGrImg(filePath);
            review.setIsPhoto("y");
        }

        return review; // Dirty Checking으로 자동 업데이트
    }

    // 리뷰 삭제 (논리 삭제)
    @Override
    public void delete(GoodsReviewDto dto, MemberDto member) throws BaCdException {
        GoodsReviewDto review = goodsReviewRepository.findById(dto.getGrno()).orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "존재하지 않는 리뷰입니다."));

        // 작성자가 맞는지 확인
        if (!review.getMember().getId().equals(member.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);
        }

        // 실제 삭제 대신 상태값 변경 (게시판 방식)
        review.setDelYn("y");
    }

    // 5. 평균 별점 계산
    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long gno) throws BaCdException {
        // 삭제되지 않은(del_yn='n') 리뷰들의 평균 점수
        return goodsReviewRepository.getAverageRatingByGno(gno);
    }

    // 가상의 파일 저장 메서드 (프로젝트 환경에 맞게 구현 필요)
    private String saveFile(MultipartFile file) throws BaCdException {
        // 저장 경로 생성 및 파일 저장 로직...
        return "/uploads/reviews/" + file.getOriginalFilename();
    }
}
