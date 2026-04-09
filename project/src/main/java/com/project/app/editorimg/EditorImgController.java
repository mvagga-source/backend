package com.project.app.editorimg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/editorimg")
public class EditorImgController {
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Value("${img.host.url}")
	private String upimgUrl;
	
	// 이미지가 저장될 실제 경로 (서버 환경에 맞춰 수정)
    private String editorPath = "editor/";

	// properties 파일에서 값을 가져옴
    @Value("${server.port}")
    private String port;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> imageUpload(@RequestParam("upload") MultipartFile upload) {
        Map<String, Object> response = new HashMap<String, Object>();

        try {
            // 1. 폴더가 없으면 생성
            File folder = new File(uploadDir, editorPath);
            if (!folder.exists()) folder.mkdirs();

            // 2. 파일명 중복 방지 (UUID 사용)
            String originalName = upload.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString() + extension;

            // 3. 파일 저장
            File saveFile = new File(folder, savedName);
            upload.transferTo(saveFile);

            // 4. CKEditor가 기대하는 JSON 형식으로 응답
            // (주의: /display/ 경로는 아래 WebConfig 설정 필요)
            response.put("uploaded", true);
            response.put("url", upimgUrl + editorPath + savedName);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("uploaded", false);
            response.put("error", Map.of("message", "파일 업로드 실패"));
            return ResponseEntity.status(500).body(response);
        }
    }
}
