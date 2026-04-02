package com.project.app.common;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import com.project.app.auth.dto.MemberDto;
import com.project.app.common.errorcode.ErrorCode;
import com.project.app.common.exception.BaCdException;

import jakarta.servlet.http.HttpSession;

public class Common {

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
            String uploadPath = "c:/upload"+pathUrl+"/"; // 전용 경로
            
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
}
