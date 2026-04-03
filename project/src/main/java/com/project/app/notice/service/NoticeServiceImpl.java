package com.project.app.notice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.app.common.exception.BaCdException;
import com.project.app.notice.dto.NoticeDto;
import com.project.app.notice.repository.NoticeRepository;

@Service
@Transactional(rollbackFor = BaCdException.class)
public class NoticeServiceImpl implements NoticeService {
	
	@Autowired
	private NoticeRepository noticeRepository;

	@Override
	public Map<String, Object> list(int page, int size, String category, String search) throws BaCdException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", noticeRepository.findAll());
		return map;
	}

	@Override
	public Map<String, Object> getMainNotices() throws BaCdException {
		List<NoticeDto> list = noticeRepository.findTop5ByDelYnAndStartDateBeforeAndEndDateAfterOrderByIsPinnedDescNnoDesc("n", LocalDateTime.now(), LocalDateTime.now());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		return map;
	}

	@Override
	public NoticeDto findById(Long nno) throws BaCdException {
		return noticeRepository.findById(nno).orElse(null);
	}

	@Override
	public NoticeDto save(NoticeDto ndto) throws BaCdException {
		return noticeRepository.save(ndto);
	}

	@Override
	public NoticeDto update(NoticeDto ndto) throws BaCdException {
		NoticeDto notice = noticeRepository.findById(ndto.getNno()).orElse(null);
		if(notice != null) {
			notice.setNtitle(ndto.getNtitle());
			notice.setNcontent(ndto.getNcontent());
			notice.setStartDate(ndto.getStartDate());
			notice.setEndDate(ndto.getEndDate());
			notice.setIsPinned(ndto.getIsPinned());
		}
		return noticeRepository.save(notice);
	}

	@Override
	public NoticeDto delete(Long nno) throws BaCdException {
		NoticeDto notice = noticeRepository.findById(nno).orElse(null);
		notice.setDelYn("y");
		return noticeRepository.save(notice);
	}

}
