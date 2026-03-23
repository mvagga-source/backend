package com.project.app.goodsreview.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    // 리뷰 등록
    @Override
    public GoodsReviewDto save(GoodsReviewDto dto, MultipartFile file, MemberDto member) throws BaCdException {
        // 이미지 처리 로직 (파일 저장 서비스가 별도로 있다면 호출)
        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file); // 가상의 파일저장 메서드
            dto.setGrImg(filePath);
            dto.setIsPhoto("y");
        }

        dto.setMember(member);
        dto.setDelYn("n"); // 초기값 세팅

        return goodsReviewRepository.save(dto);
    }

    // 리뷰 수정
    @Override
    public GoodsReviewDto update(GoodsReviewDto dto, MultipartFile file, MemberDto member) throws BaCdException {
        GoodsReviewDto existReview = goodsReviewRepository.findById(dto.getGrno())
                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "존재하지 않는 리뷰입니다."));

        // 권한 확인
        if (!existReview.getMember().getId().equals(member.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);
        }

        // 내용 및 별점 업데이트
        existReview.setGrcontents(dto.getGrcontents());
        existReview.setRating(dto.getRating());

        // 새 이미지 첨부 시 업데이트
        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file);
            existReview.setGrImg(filePath);
            existReview.setIsPhoto("y");
        }

        return existReview; // Dirty Checking으로 자동 업데이트
    }

    // 리뷰 삭제 (논리 삭제)
    @Override
    public void delete(Long grno, MemberDto member) throws BaCdException {
        GoodsReviewDto existReview = goodsReviewRepository.findById(grno)
                .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "존재하지 않는 리뷰입니다."));

        if (!existReview.getMember().getId().equals(member.getId())) {
            throw new BaCdException(ErrorCode.AUTH_USER_NOT_MATCH);
        }

        // 실제 삭제 대신 상태값 변경 (게시판 방식)
        existReview.setDelYn("y");
    }

    // 리뷰 목록 조회
    /*@Override
    @Transactional(readOnly = true)
    public List<GoodsReviewDto> getListByGno(Long gno) {
        // Repository에서 Query를 통해 del_yn 관계없이 
        // 계층 구조(parent-child)를 고려하여 가져오는 로직 필요
        return goodsReviewRepository.findByGoods_GnoOrderByGrnoDesc(gno);
    }

    // 5. 평균 별점 계산
    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long gno) {
        // 삭제되지 않은(del_yn='n') 리뷰들의 평균 점수
        return goodsReviewRepository.getAverageRatingByGno(gno);
    }*/

    // 가상의 파일 저장 메서드 (프로젝트 환경에 맞게 구현 필요)
    private String saveFile(MultipartFile file) {
        // 저장 경로 생성 및 파일 저장 로직...
        return "/uploads/reviews/" + file.getOriginalFilename();
    }
}
