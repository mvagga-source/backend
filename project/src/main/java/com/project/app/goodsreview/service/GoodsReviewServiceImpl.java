package com.project.app.goodsreview.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.Common;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;
import com.project.app.goodsReviewLike.repository.GoodsReviewLikeRepository;
import com.project.app.goodsreview.dto.GoodsReviewDto;
import com.project.app.goodsreview.repository.GoodsReviewRepository;

import jakarta.servlet.http.HttpSession;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class GoodsReviewServiceImpl implements GoodsReviewService {
	@Autowired
    private GoodsReviewRepository goodsReviewRepository;
	
	@Autowired
	private GoodsReviewLikeRepository goodsReviewLikeRepository;
	
	@Autowired
	private HttpSession session;
	
	@Value("${img.host.url}")
    private String imgHostUrl;
	
	// 리뷰 목록 조회
    /*@Override
    @Transactional(readOnly = true)
    public Map<String, Object> findAll(Long gno, int size, Long lastGrno, String sortDir, Long lastLikeCnt, Double lastRating) throws BaCdException {
    	// 1. 이번 페이지의 마지노선이 될 'size번째 정상 리뷰'의 ID를 찾음
        Long targetGrno = goodsReviewRepository.findTargetGrno(gno, lastGrno, size);
        
        List<GoodsReviewDto> list;
        boolean isLast = false;

        if (targetGrno == null) {
            // 더 이상 정상 리뷰가 size만큼 남아있지 않은 경우 -> 남은 모든 원글을 가져옴
            list = goodsReviewRepository.findAllInRange(gno, 0L, lastGrno);
            isLast = true;
        } else {
            // 마지노선 ID를 포함하여 그 사이의 모든 원글(삭제포함)을 가져옴
            list = goodsReviewRepository.findAllInRange(gno, targetGrno, lastGrno);
            
            // 가져온 리스트 중 '정상 리뷰'의 개수를 세어서 마지막 페이지 여부 확인
            long normalCount = list.stream().filter(r -> "n".equals(r.getDelYn())).count();
            if (normalCount < size) isLast = true; 
        }

        // 2. 응답 구성에 필요한 마지막 '정상' ID 추출 (다음 요청의 lastGrno가 됨)
        Long nextLastGrno = 0L;
        if (!list.isEmpty()) {
            // 리스트를 뒤에서부터 탐색하여 가장 마지막에 위치한 '정상' 리뷰의 ID를 찾음
            nextLastGrno = list.stream()
                .filter(r -> "n".equals(r.getDelYn()))
                .map(GoodsReviewDto::getGrno)
                .reduce((first, second) -> second) // 마지막 요소 추출
                .orElse(list.get(list.size() - 1).getGrno()); // 정상글 없으면 그냥 마지막 글 ID
        }

        MemberDto member = (MemberDto) session.getAttribute("user");
        list.forEach(r -> {
        	// 전체 좋아요 개수 조회 및 세팅
            long totalLike = goodsReviewLikeRepository.countByReview_Grno(r.getGrno());
            r.setLikeCnt(totalLike);

            // 내가 눌렀는지 여부 체크 (로그인 했을 때만)
            if (member != null) {
                boolean liked = goodsReviewLikeRepository.existsByReview_GrnoAndMember_Id(r.getGrno(), member.getId());
                r.setLiked(liked);
            }
            // 보안 처리: 삭제된 글의 민감 정보 제거
            if ("y".equals(r.getDelYn())) {
                r.setGrcontents("작성자에 의해 삭제된 리뷰입니다.");
                r.setGrImg(null);
                r.setRating(0.0);
            }
        });

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", goodsReviewRepository.totCntGoodsReview(gno));
        result.put("lastGrno", nextLastGrno);
        result.put("isLast", isLast);

        return result;
    }*/
    
	// 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findAll(Long gno, int size, Long lastGrno, String sortDir, Long lastLikeCnt, Double lastRating) {
        //문제가 많은경우 정렬 빼고 위에걸로
    	
        // 1. 삭제된 글이 섞여 있을 것을 대비해 넉넉하게 가져오되, 
        // 다음 페이지가 있는지 확인하기 위해 '하나 더' 체크하는 개념이 필요합니다.
        int fetchLimit = size * 2; // 삭제글이 많을 수 있으니 더 넉넉히
        
        List<Long> ids = goodsReviewRepository.findIdsInRange(
            gno, lastGrno, lastLikeCnt, lastRating, sortDir, fetchLimit
        );

        if (ids.isEmpty()) {
            return Map.of("list", Collections.emptyList(), "isLast", true, "totalCount", 0L);
        }

        // 2. 상세 데이터 조회 및 순서 유지 (기존과 동일)
        List<GoodsReviewDto> rawList = goodsReviewRepository.findAllByGrnoIn(ids);
        List<GoodsReviewDto> sortedList = ids.stream()
            .map(id -> rawList.stream().filter(r -> r.getGrno().equals(id)).findFirst().orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 3. 결과 필터링
        List<GoodsReviewDto> resultList = new ArrayList<>();
        int normalCount = 0;
        int lastProcessedIndex = -1; // 어디까지 읽었는지 기록

        MemberDto loginMember = (MemberDto) session.getAttribute("user");

        for (int i = 0; i < sortedList.size(); i++) {
            GoodsReviewDto review = sortedList.get(i);
            lastProcessedIndex = i; // 현재 인덱스 저장

            // 후처리 (좋아요 등 - 기존 로직 유지)
            long currentLikeCnt = goodsReviewLikeRepository.countByReview_Grno(review.getGrno());
            review.setLikeCnt(currentLikeCnt);
            if (loginMember != null) {
                review.setLiked(goodsReviewLikeRepository.existsByReview_GrnoAndMember_Id(review.getGrno(), loginMember.getId()));
            }

            if ("y".equals(review.getDelYn())) {
                review.setGrcontents("작성자에 의해 삭제된 리뷰입니다.");
                review.setGrImg(null);
                review.setRating(0.0);
            } else {
                normalCount++;
            }

            resultList.add(review);
            
            // ★ 핵심: 정상글을 size만큼 다 채웠다면 멈춤
            if (normalCount == size) break;
        }

        // 4. 다음 커서 정보 추출 (정상글/삭제글 상관없이 리스트의 마지막 항목 기준)
        Long nextLastGrno = 0L;
        long nextLastLikeCnt = 0;
        double nextLastRating = 0.0;

        if (!resultList.isEmpty()) {
            GoodsReviewDto lastEntry = resultList.get(resultList.size() - 1);
            nextLastGrno = lastEntry.getGrno();
            nextLastLikeCnt = lastEntry.getLikeCnt();
            // 삭제글은 rating이 0.0으로 덮어씌워졌으므로 원본(ids 순서에서 찾은 값) 사용이 안전함
            nextLastRating = sortedList.get(lastProcessedIndex).getRating();
        }

        // 5. 다음 페이지 존재 여부 (isLast) 판정
        // - 뽑아온 전체 ID 개수보다 내가 처리한 인덱스가 작다면 뒤에 더 있는 것임
        boolean hasMore = (ids.size() > lastProcessedIndex + 1);

        Map<String, Object> result = new HashMap<>();
        result.put("list", resultList);
        result.put("totalCount", goodsReviewRepository.totCntGoodsReview(gno));
        result.put("lastGrno", nextLastGrno);
        result.put("lastLikeCnt", nextLastLikeCnt);
        result.put("lastRating", nextLastRating);
        result.put("isLast", !hasMore); 
        result.put("success", true);

        return result;
    }

    // 리뷰 등록
	@Transactional
    @Override
    public GoodsReviewDto save(GoodsReviewDto dto, MultipartFile file, MemberDto member) throws BaCdException {
        // 이미지 처리 로직 (파일 저장 서비스가 별도로 있다면 호출)
        if (file != null && !file.isEmpty()) {
            String filePath = Common.saveFile(file, imgHostUrl, "goodsReview");
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

        // 새 파일은 없는데, 프론트에서 기존 이미지를 삭제한 경우 (dto의 grImg가 비어있거나 삭제 플래그 확인)
        if (dto.getGrImg() == null || dto.getGrImg().isEmpty()) {
        	// 기존 파일이 있었다면 물리적 삭제
        	if (review.getGrImg() != null) {
        		Common.deleteFile(review.getGrImg(), "goodsReview");
        	}
        	review.setGrImg(null);
        	review.setIsPhoto("n");
        }
        // 새 이미지 첨부 시 업데이트(기존에 있던건 앞조건에서 삭제후 다시 업로드)
        if (file != null && !file.isEmpty()) {
            String filePath = Common.saveFile(file, imgHostUrl, "goodsReview");
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
        // 삭제 되었다는 메세지로 조회할때 표기할 예정이라 파일삭제 미구현

        // 실제 삭제 대신 상태값 변경 (게시판 방식)
        review.setDelYn("y");
    }
    
    @Transactional
    @Override
    public GoodsReviewDto reply(GoodsReviewDto dto, MemberDto member) throws BaCdException {
        // 1. 기본 정보 설정
        dto.setMember(member);
        dto.setDelYn("n");
        dto.setIsPhoto("n");

        // 2. 답글(부모가 있는 경우) 처리
        if (dto.getParent() != null && dto.getParent().getGrno() != null) {
            // 실제 부모 리뷰가 존재하는지 확인
            GoodsReviewDto parent = goodsReviewRepository.findById(dto.getParent().getGrno())
                    .orElseThrow(() -> new BaCdException(ErrorCode.NOT_FOUND, "부모 리뷰가 존재하지 않습니다."));
            
            dto.setParent(parent);
            dto.setRating(0.0); // 답글은 별점 제외
            dto.setIsPhoto("n"); // 답글은 사진 제외 (필요시 수정)
        }

        return goodsReviewRepository.save(dto);
    }

    // 평균 별점 계산
    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long gno) throws BaCdException {
        // 삭제되지 않은(del_yn='n') 리뷰들의 평균 점수
        return goodsReviewRepository.getAverageRatingByGno(gno);
    }
}
