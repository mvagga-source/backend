package com.project.app.admin.service;

import java.util.Map;

import com.project.app.video.dto.VideoRequestParams;

public interface AdminVideoService {

	void save(Long id);

	Map<String, Object> findVideoList(VideoRequestParams params);

}
