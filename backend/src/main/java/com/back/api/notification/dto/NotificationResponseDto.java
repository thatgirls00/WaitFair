package com.back.api.notification.dto;

import java.time.LocalDateTime;

// 알림 목록 응답용 DTO

public record NotificationResponseDto(
	Long id,
	String type,          // enum name
	String typeDetail,
	String title,
	String message,
	boolean isRead,
	LocalDateTime createdAt,
	LocalDateTime readAt
) { }
