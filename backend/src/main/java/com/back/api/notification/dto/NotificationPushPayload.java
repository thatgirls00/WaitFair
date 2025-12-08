package com.back.api.notification.dto;

public record NotificationPushPayload(
	String type,                       // "NEW_NOTIFICATION"
	long unreadCount,
	NotificationResponseDto notification
) { }

