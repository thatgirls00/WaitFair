package com.back.api.notification.dto;

public record UnreadCountResponseDto(// 읽지 않은 알림 개수 응답용 DTO
	long unreadCount
) { }
