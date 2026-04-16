package com.project.app.common;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;

public class Common {
	
	/**
	 * 관리자인지 체크
	 * @param session
	 * @return
	 * @throws BaCdException
	 */
	public static MemberDto adminIdCheck(HttpSession session) throws BaCdException {
		MemberDto memberDto = idCheck(session);
		if(!"admin".equals(memberDto.getId())) {
			throw new BaCdException(ErrorCode.AUTH_ADMIN_NOT_FOUND);
		}
		return memberDto;
	}

	/**
	 * 로그인 체크
	 * @param session
	 * @return
	 * @throws BaCdException
	 */
	public static MemberDto idCheck(HttpSession session) throws BaCdException {
		MemberDto memberDto = (MemberDto) session.getAttribute("user");
		if(memberDto == null) {
			throw new BaCdException(ErrorCode.UNAUTHORIZED);	//로그인 후 사용
		}
		return memberDto;
	}
	
	/**
	 * 파일 저장 메서드
	 * @param file				//파일
	 * @param imgHostUrl		//공통에 있는 application.properties에 있는 localhost:8181(사용안함 리액트의 환경설정의 주소로 변경)
	 * @param pathUrl			//upload안에 중간 저장 경로
	 * @return
	 * @throws BaCdException
	 */
	public static String saveFile(MultipartFile file, String imgHostUrl, String pathUrl) throws BaCdException {
    	// 파일 업로드 처리 (게시판 로직 응용)
        if(file != null && !file.isEmpty()) {
            String fName = file.getOriginalFilename();
            String refName = System.currentTimeMillis() + "_" + fName;
            String uploadPath = "c:/upload/"+pathUrl+"/"; // 전용 경로
            
            try {
                File folder = new File(uploadPath);
                if(!folder.exists()) folder.mkdirs();
                file.transferTo(new File(uploadPath + refName));
                return "/"+pathUrl+"/" + refName; // 리턴 받은 파일경로를 DTO에 저장
                //return imgHostUrl+"/"+pathUrl+"/" + refName; // 리턴 받은 파일경로를 DTO에 저장 localhost:8181/upload/+pathUrl/+refName
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		return null;
    }
	
	/**
	 * 파일 저장 메서드(오버로딩으로 위에와 동일)
	 * @param file
	 * @param imgHostUrl
	 * @param pathUrl
	 * @return
	 * @throws BaCdException
	 */
	public static String saveFile(MultipartFile file, String pathUrl) throws BaCdException {
    	// 파일 업로드 처리 (게시판 로직 응용)
        if(file != null && !file.isEmpty()) {
            String fName = file.getOriginalFilename();
            String refName = System.currentTimeMillis() + "_" + fName;
            String uploadPath = "c:/upload/"+pathUrl+"/"; // 전용 경로
            
            try {
                File folder = new File(uploadPath);
                if(!folder.exists()) folder.mkdirs();
                file.transferTo(new File(uploadPath + refName));
                return "/"+pathUrl+"/" + refName; // 리턴 받은 파일경로를 DTO에 저장
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		return null;
    }

	/**
	 * 파일 삭제 메서드
	 * @param filePath DB에 저장된 전체 경로 (예: /goodsReview/12345_test.jpg)
	 * @param pathUrl  중간 경로 (예: goodsReview)
	 */
	public static void deleteFile(String filePath, String pathUrl) {
	    if (filePath == null || filePath.isEmpty()) return;

	    try {
	        // 1. DB에 저장된 URL에서 파일명만 추출
	        // 마지막 '/' 이후의 문자열이 파일명입니다.
	        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
	        
	        // 2. 물리적 경로 생성 (saveFile과 동일한 기본 경로 사용)
	        String fullPath = "c:/upload/" + pathUrl + "/" + fileName;
	        
	        File file = new File(fullPath);
	        
	        // 3. 파일이 존재하면 삭제
	        if (file.exists()) {
	            if (file.delete()) {
	                System.out.println("파일 삭제 성공: " + fullPath);
	            } else {
	                System.out.println("파일 삭제 실패: " + fullPath);
	            }
	        }
	    } catch (Exception e) {
	        System.err.println("파일 삭제 중 오류 발생: " + e.getMessage());
	    }
	}
	
	/**
	 * 유연한 날짜 파서
	 * 지원:
	 * yyyy
	 * yyyy-MM
	 * yyyy-MM-dd
	 */
	public static LocalDateTime parseFlexibleDate(Object value, boolean isStart) {
	    if (value == null) return null;

	    String str = value.toString().trim();

	    try {
	        // 1) yyyy (연도만)
	        if (str.matches("\\d{4}")) {
	            int year = Integer.parseInt(str);
	            return isStart
	                    ? LocalDate.of(year, 1, 1).atStartOfDay()
	                    : LocalDate.of(year, 12, 31).atTime(23, 59, 59);
	        }

	        // 2) yyyy-MM
	        if (str.matches("\\d{4}-\\d{2}")) {
	            LocalDate date = LocalDate.parse(str + "-01");
	            return isStart
	                    ? date.withDayOfMonth(1).atStartOfDay()
	                    : date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59, 59);
	        }

	        // 3) yyyy-MM-dd
	        if (str.matches("\\d{4}-\\d{2}-\\d{2}")) {
	            LocalDate date = LocalDate.parse(str);
	            return isStart
	                    ? date.atStartOfDay()
	                    : date.atTime(23, 59, 59);
	        }

	        // 4) yyyy-MM-dd HH:mm:ss (옵션)
	        if (str.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
	            return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        }

	    } catch (Exception e) {
	        throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE,"날짜 형식이 올바르지 않습니다.");
	    }

	    throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE,"지원하지 않는 날짜 형식입니다.");
	}
	
	public static LocalDateTime parseFlexibleDateTui(Object value, boolean isStart) {
		if (value == null) return null;

	    String str = value.toString().trim();

	    try {

	        // 🔥 핵심: 깨진 ss 보정
	        if (str.contains(":ss")) {
	            str = str.replace(":ss", ":00");
	        }

	        // yyyy-MM-dd HH:mm:ss
	        if (str.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
	            return LocalDateTime.parse(str,
	                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        }

	        // yyyy-MM-dd HH:mm
	        if (str.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")) {
	            return LocalDateTime.parse(str + ":00",
	                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        }

	        // yyyy-MM-dd
	        if (str.matches("\\d{4}-\\d{2}-\\d{2}$")) {
	            LocalDate date = LocalDate.parse(str);
	            return isStart ? date.atStartOfDay()
	                           : date.atTime(23, 59, 59);
	        }

	    } catch (Exception e) {
	        throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "날짜 형식 오류: " + str);
	    }

	    throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 날짜 형식: " + str);
	}
	
	public static LocalDateTime parseDate(String date, boolean isStart) {
	    if (date == null || date.isEmpty()) return null;

	    try {
	        String str = date.trim().replace("T", " ");

	        if (str.matches("\\d{4}-\\d{2}-\\d{2}$")) {
	            str += isStart ? " 00:00:00" : " 23:59:59";
	        }

	        if (str.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")) {
	            str += ":00";
	        }

	        return LocalDateTime.parse(str,
	                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	    } catch (Exception e) {
	        throw new BaCdException(ErrorCode.INVALID_INPUT_VALUE,
	                "날짜 형식 오류: " + date);
	    }
	}
}
