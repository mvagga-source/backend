package com.project.app.admin.controller;

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
@RequestMapping("/admin/download")
public class AdminDownLoadController {
	// 이미지가 저장될 실제 경로 (서버 환경에 맞춰 수정)
    @Value("${file.upload-dir}")
	public String uploadDir;

	// properties 파일에서 값을 가져옴
    @Value("${server.port}")
    private String port;
    
    /**
     * 에디터와는 다른 파일 다운로드
     * @param fileName
     * @param response
     * @throws Exception
     */
    @GetMapping("/download")
    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) throws Exception {
        String filePath = uploadDir + fileName;
        File file = new File(filePath);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", 
            "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");

        FileInputStream fis = new FileInputStream(file);
        OutputStream os = response.getOutputStream();

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        fis.close();
        os.flush();
        os.close();
    }
}
