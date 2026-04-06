package com.project.app.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public class GridUtils {
	/**
     * TUI Grid 응답 규격에 맞게 데이터를 변환
     */
	public static Map<String, Object> gridRes(Page<?> page) {
	    Map<String, Object> pagination = new HashMap<>();
	    // JPA Page는 0부터 시작하므로 +1
	    pagination.put("page", page.getNumber() + 1); 
	    pagination.put("totalCount", page.getTotalElements());

	    Map<String, Object> data = new HashMap<>();
	    data.put("contents", page.getContent()); // Entity 리스트가 들어감
	    data.put("pagination", pagination);
	    
	    Map<String, Object> map = new HashMap<>();
	    map.put("result", true);
	    map.put("data", data);
	    return map;
	}

	/**
     * TUI Grid 응답 규격에 맞게 데이터를 변환
     */
	public static Map<String, Object> gridRes(Page<?> page, int size) {
	    Map<String, Object> pagination = new HashMap<>();
	    // JPA Page는 0부터 시작하므로 +1
	    pagination.put("page", page.getNumber() + 1); 
	    pagination.put("perPage", size);
	    pagination.put("totalCount", page.getTotalElements());

	    Map<String, Object> data = new HashMap<>();
	    data.put("contents", page.getContent()); // Entity 리스트가 들어감
	    data.put("pagination", pagination);
	    
	    Map<String, Object> map = new HashMap<>();
	    map.put("result", true);
	    map.put("data", data);
	    return map;
	}
}
