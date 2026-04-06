package com.project.app.admin.service;

import java.util.Map;

import com.project.app.common.exception.BaCdException;

public interface AdminGoodsService {
	public Map<String, Object> getSummaryData() throws BaCdException;
}
