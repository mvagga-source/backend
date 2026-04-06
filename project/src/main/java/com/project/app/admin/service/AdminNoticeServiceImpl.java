package com.project.app.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.project.app.admin.repository.AdminGoodsOrdersRepository;
import com.project.app.admin.repository.AdminNoticeRepository;
import com.project.app.common.Common;
import com.project.app.common.GridUtils;
import com.project.app.common.exception.BaCdException;
import com.project.app.notice.dto.NoticeDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = BaCdException.class)
public class AdminNoticeServiceImpl implements AdminNoticeService {
	
	private final AdminNoticeRepository adminNoticeRepository;
	
	private final HttpSession session;

	@Override
	public Map<String, Object> ajaxList(int page, int perPage, String category, String search, String sortDir, String sortBy,
			String startDate, String endDate) {
		// 페이징 설정
	    Pageable pageable = PageRequest.of(page - 1, perPage);

	    // 2. 날짜 문자열을 LocalDateTime으로 변환 (검색 조건이 있을 때만)
	    LocalDateTime startDt = null;
	    LocalDateTime endDt = null;
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	    if (startDate != null && !startDate.isEmpty()) {
	        startDt = LocalDate.parse(startDate, formatter).atStartOfDay();
	    }
	    if (endDate != null && !endDate.isEmpty()) {
	        endDt = LocalDate.parse(endDate, formatter).atTime(LocalTime.MAX);
	    }

	    // 3. 레포지토리 호출
	    Page<NoticeDto> resultPage = adminNoticeRepository.findAllNoticeWithFilter(
	            category, search, startDate, startDt, endDate, endDt, pageable
	    );
		return GridUtils.gridRes(resultPage, perPage);
	}
	
	@Override
    public Map<String, Object> saveAll(Map<String, Object> param) {
		// 삭제(Deleted) 데이터 처리 (그리드에서 행 삭제 후 저장 누른 경우)
		/*List<Map<String, Object>> deletedRows = (List<Map<String, Object>>) param.get("deletedRows");
		if (deletedRows != null) {
			deletedRows.forEach(row -> {
				Long nno = Long.valueOf(row.get("nno").toString());
				adminNoticeRepository.deleteById(nno);
			});
		}*/
		processRows((List<Map<String, Object>>) param.get("createdRows"));		//하나씩 수정
		processRows((List<Map<String, Object>>) param.get("updatedRows"));
		
		/*List<NoticeDto> saveList = new java.util.ArrayList<>();
        collectRows((List<Map<String, Object>>) param.get("createdRows"), saveList);		//리스트 saveAll로 수정
        collectRows((List<Map<String, Object>>) param.get("updatedRows"), saveList);
        if (!saveList.isEmpty()) {
            adminNoticeRepository.saveAll(saveList);
        }*/
        Map<String, Object> map = new HashMap<>();
		map.put("result", true);

        return map;
    }

	//행추가된 데이터 및 수정된 데이터들
	private void processRows(List<Map<String, Object>> rows) {
	    if (rows == null || rows.isEmpty()) return;

	    rows.forEach(row -> {
	        NoticeDto.NoticeDtoBuilder builder = NoticeDto.builder();

	        // PK 및 기본 필드
	        if (row.get("nno") != null && !row.get("nno").toString().isEmpty()) {
	            builder.nno(Long.valueOf(row.get("nno").toString()));
	        }

	        builder.ntitle((String) row.get("ntitle"))
	               .ncontent((String) row.get("ncontent"))
	               .delYn(row.get("delYn") != null ? (String) row.get("delYn") : "n");

	        // 안전한 LocalDateTime 변환
	        builder.startDate(safeParseDateTime(row.get("startDate"), true));
	        builder.endDate(safeParseDateTime(row.get("endDate"), false));

	        adminNoticeRepository.save(builder.build());
	    });
	}

	/**
	 * 문자열을 LocalDateTime으로 안전하게 변환
	 * @param value  날짜 문자열
	 * @param isStart 시작일이면 true, 종료일이면 false
	 * @return LocalDateTime
	 */
	private LocalDateTime safeParseDateTime(Object value, boolean isStart) {
	    if (value == null) return null;

	    String str = value.toString().trim().replace("T", " ");

	    // 기본값 보정: 초가 없으면 :00 붙이기, 종료일이면 시:분:초 23:59:59
	    try {
	        if (str.matches("\\d{4}-\\d{2}-\\d{2}$")) { // yyyy-MM-dd
	            str += isStart ? " 00:00:00" : " 23:59:59";
	        } else if (str.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")) { // yyyy-MM-dd HH:mm
	            str += ":00";
	        } else if (str.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*")) {
	            str = str.substring(0, 19); // yyyy-MM-dd HH:mm:ss만 사용
	        } else {
	            // 예외 문자열 들어오면 기본값
	            str = isStart ? LocalDate.now().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
	                          : LocalDate.now().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        }

	        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	    } catch (Exception e) {
	        // 안전하게 null 리턴
	        return null;
	    }
	}
    
    /**
     * Map 리스트를 엔티티 리스트로 변환하여 수집
     */
    private void collectRows(List<Map<String, Object>> rows, List<NoticeDto> saveList) {
        if (rows != null && !rows.isEmpty()) {
        	ObjectMapper objectMapper = new ObjectMapper();
            rows.forEach(row -> {
                NoticeDto notice = objectMapper.convertValue(row, NoticeDto.class);
                saveList.add(notice);
            });
        }
    }

    //행 삭제로직
    @Override
    public Map<String, Object> deleteAll(Map<String, Object> param) {
        List<Map<String, Object>> rows = (List<Map<String, Object>>) param.get("deletedRows");
        if (rows != null) {
            rows.forEach(row -> {
                Long nno = Long.valueOf(row.get("nno").toString());
                adminNoticeRepository.deleteById(nno);
            });
        }
        Map<String, Object> map = new HashMap<>();
        map.put("resultCnt", rows.size());
        map.put("result", true);
		return map;
    }

}
