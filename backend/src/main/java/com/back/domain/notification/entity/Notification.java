package com.back.domain.notification.entity;

import java.time.LocalDateTime;

import com.back.domain.event.entity.Event;
import com.back.domain.notification.model.NotificationTypeDetails;
import com.back.domain.notification.model.NotificationTypes;
import com.back.domain.user.entity.User;
import com.back.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

//TODO: User 엔티티쪽에 Cascade 설정 해줘야 함
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationTypes type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationTypeDetails typeDetail;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private boolean isRead = false;

	@Column(nullable = true)
	private LocalDateTime readAt;

	//연관 필드
	@ManyToOne(fetch = FetchType.LAZY) // -> 리팩토링 고민요소
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	@ManyToOne(fetch = FetchType.LAZY) // -> 리팩토링 고민요소
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	private Notification(NotificationTypes type,
		NotificationTypeDetails typeDetail,
		String title,
		String message,
		User user,
		Event event) {
		this.type = type;
		this.typeDetail = typeDetail;
		this.title = title;
		this.message = message;
		this.user = user;
		this.event = event;
		this.isRead = false;
		this.readAt = null;
	}

	public static Notification create(NotificationTypes type,
		NotificationTypeDetails typeDetail,
		String title,
		String message,
		User user,
		Event event) {
		return new Notification(type, typeDetail, title, message, user, event);
	}

	public void markAsRead() {
		if (!this.isRead) {
			this.isRead = true;
			this.readAt = LocalDateTime.now();
		}
	}

}
