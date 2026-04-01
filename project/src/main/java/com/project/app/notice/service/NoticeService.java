package com.project.app.notice.service;

import java.util.List;
import java.util.Map;

import com.project.app.common.exception.BaCdException;
import com.project.app.notice.dto.NoticeDto;

public interface NoticeService {
	public Map<String, Object> list(int page, int size, String category, String search) throws BaCdException;
	
	public Map<String, Object> getMainNotices() throws BaCdException;
	
	public NoticeDto findById(Long nno) throws BaCdException;
	
	public NoticeDto save(NoticeDto ndto) throws BaCdException;
	
	public NoticeDto update(NoticeDto ndto) throws BaCdException;
	
	public NoticeDto delete(Long nno) throws BaCdException;
}
