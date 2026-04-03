package com.project.app.notificationSetting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.app.notificationSetting.dto.NotificationSettingDto;

public interface NotificationSettingRepository extends JpaRepository<NotificationSettingDto, String> {

}