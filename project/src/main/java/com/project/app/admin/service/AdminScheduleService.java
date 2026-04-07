package com.project.app.admin.service;

import java.util.Map;

import com.project.app.admin.dto.schedule.ScheduleRequestParams;
import com.project.app.schedule.dto.EventDto;

public interface AdminScheduleService {

	Map<String, Object> EventList(ScheduleRequestParams params);

	void EventDelete(Long eno);

	void EventSave(Long eno, String title, String startDate, String endDate, String highlightFlag, String description);

}
