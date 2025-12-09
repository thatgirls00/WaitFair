package com.back.api.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.back.api.notification.dto.NotificationResponseDto;

public interface NotificationService {
	Page<NotificationResponseDto> getNotifications(Long userId, String status, Pageable pageable);
}
