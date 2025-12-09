package com.back.domain.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.back.domain.notification.entity.Notification;
import com.back.domain.user.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	// 전체 조회
	Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	// 읽지 않은 것만 조회
	Page<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user, Pageable pageable);
}
