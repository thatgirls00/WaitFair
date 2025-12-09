package com.back.api.notification.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {


	public NotificationController() {

	}



	// /**
	//  * 알림 목록 조회
	//  * - status: ALL / UNREAD
	//  * - pageable: page, size, sort
	//  */
	// @GetMapping
	// public ResponseEntity<Page<NotificationResponseDto>> getNotifications(
	// 	@AuthenticationPrincipal CustomUserDetails user,
	// 	@RequestParam(defaultValue = "ALL") String status,
	// 	@PageableDefault(size = 20) Pageable pageable
	// ) {
	// 	Long userId = user.getId();
	// 	Page<NotificationResponseDto> notifications =
	// 		notificationService.getNotifications(userId, status, pageable);
	//
	// 	return ResponseEntity.ok(notifications);
	// }
	//
	// /**
	//  * 읽지 않은 알림 개수 조회
	//  */
	// @GetMapping("/unread-count")
	// public ResponseEntity<UnreadCountResponseDto> getUnreadCount(
	// 	@AuthenticationPrincipal CustomUserDetails user
	// ) {
	// 	Long userId = user.getId();
	// 	long unreadCount = notificationService.getUnreadCount(userId);
	//
	// 	return ResponseEntity.ok(new UnreadCountResponseDto(unreadCount));
	// }
	//
	// /**
	//  * 개별 알림 읽음 처리
	//  */
	// @PatchMapping("/{notificationId}/read")
	// public ResponseEntity<UnreadCountResponseDto> markAsRead(
	// 	@AuthenticationPrincipal CustomUserDetails user,
	// 	@PathVariable Long notificationId
	// ) {
	// 	Long userId = user.getId();
	// 	long unreadCount = notificationService.markAsRead(userId, notificationId);
	//
	// 	// unreadCount 를 응답에 실어주면 프론트에서 뱃지 바로 갱신 가능
	// 	return ResponseEntity.ok(new UnreadCountResponseDto(unreadCount));
	// 	// 또는 ResponseEntity.noContent().build(); 형태도 가능 (그 경우 프론트가 /unread-count 다시 호출)
	// }
	//
	// /**
	//  * 전체 알림 읽음 처리
	//  */
	// @PatchMapping("/read-all")
	// public ResponseEntity<UnreadCountResponseDto> markAllAsRead(
	// 	@AuthenticationPrincipal CustomUserDetails user
	// ) {
	// 	Long userId = user.getId();
	// 	long unreadCount = notificationService.markAllAsRead(userId); // 거의 항상 0일 것
	//
	// 	return ResponseEntity.ok(new UnreadCountResponseDto(unreadCount));
	// }
}
