package com.back.api.notification.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.api.notification.dto.NotificationResponseDto;
import com.back.api.notification.dto.UnreadCountResponseDto;
import com.back.api.notification.service.NotificationService;
import com.back.domain.user.entity.User;
import com.back.global.http.HttpRequestContext;
import com.back.global.response.ApiResponse;
import com.back.global.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationApi {

	private final NotificationService notificationService;
	private final HttpRequestContext httpRequestContext;

	@Override
	@GetMapping
	public ApiResponse<List<NotificationResponseDto>> getNotifications(
	) {
		User user = httpRequestContext.getUser();

		List<NotificationResponseDto> notifications =
			notificationService.getNotifications(user.getId());

		return ApiResponse.ok("알림 목록을 불러왔습니다",notifications);
	}

	/**
	 * 읽지 않은 알림 개수 조회
	 */
	@Override
	@GetMapping("/unread-count")
	public ApiResponse<UnreadCountResponseDto> getUnreadCount(
	) {
		User user = httpRequestContext.getUser();
		long count = notificationService.getUnreadCount(user.getId());
		return ApiResponse.ok("읽지 않은 알림수",new UnreadCountResponseDto(count));
	}

	/**
	 * 개별 알림 읽음 처리
	 */
	@Override
	@PatchMapping("/{notificationId}/read")
	public ApiResponse<Void> markAsRead(
		@PathVariable Long notificationId
	) {
		User user = httpRequestContext.getUser();
		notificationService.markAsRead(notificationId, user.getId());

		return ApiResponse.noContent("개별 알림을 읽음 처리 하였습니다.");
	}

	/**
	 * 전체 알림 읽음 처리
	 */
	@Override
	@PatchMapping("/read-all")
	public ApiResponse<Void> markAllAsRead() {
		User user = httpRequestContext.getUser();
		notificationService.markAllAsRead(user.getId());
		return ApiResponse.noContent("모든 알림을 읽음 처리 하였습니다.");
	}
}
