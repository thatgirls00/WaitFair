package com.back.api.queue.dto.response;

import com.back.domain.queue.entity.QueueEntryStatus;

// 입장 준비 완료 응답 DTO
public record EnteredQueueResponse(
	Long userId,
	Long eventId,
	QueueEntryStatus status


){
}
