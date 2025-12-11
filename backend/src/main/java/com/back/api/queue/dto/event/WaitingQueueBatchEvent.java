package com.back.api.queue.dto.event;

import java.util.Map;

import com.back.api.queue.dto.response.WaitingQueueResponse;

// 대기열 일괄 업데이트 이벤트
// broadcast 방식으로 모든 대기 사용자에게 한번에 전송
public record WaitingQueueBatchEvent(
	Long eventId,
	Map<Long, WaitingQueueResponse> updates
) {
	public static WaitingQueueBatchEvent from(Long eventId, Map<Long, WaitingQueueResponse> updates) {
		return new WaitingQueueBatchEvent(eventId, updates);
	}
}
