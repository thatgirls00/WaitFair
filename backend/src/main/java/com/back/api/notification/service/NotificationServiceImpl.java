package com.back.api.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.back.api.notification.dto.NotificationResponseDto;
import com.back.domain.notification.entity.Notification;
import com.back.domain.notification.repository.NotificationRepository;
import com.back.domain.user.entity.User;
import com.back.domain.user.repository.UserRepository;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	public NotificationServiceImpl(NotificationRepository notificationRepository,
		UserRepository userRepository) {
		this.notificationRepository = notificationRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Page<NotificationResponseDto> getNotifications(Long userId, String status, Pageable pageable) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

		Page<Notification> page;

		if ("UNREAD".equalsIgnoreCase(status)) {
			page = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user, pageable);
		} else {
			page = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		}

		return page.map(NotificationResponseDto::fromEntity);
	}
}
