package com.back.api.queue.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.back.api.queue.dto.event.WaitingQueueBatchEvent;
import com.back.api.queue.dto.response.QueueEntryStatusResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
//TODO 로그 제거
public class QueueEntryWebSocketPublisher {

	private final SimpMessagingTemplate messagingTemplate;

	//unicast - 입장/만료/결제 완료 처리
	public void publisherToUser(QueueEntryStatusResponse response) {

		String destination = "/topic/users/" + response.userId() + "/queue";

		//기존 response DTO를 JSON으로 변환해서 전달
		//messagingTemplate.convertAndSend(destination, response);

		//테스트용 로그
		try {
			messagingTemplate.convertAndSend(destination, response);

			log.info("WebSocket 발행 성공 - userId: {}, type: {}",
				response.userId(), response.getClass().getSimpleName());

		} catch (Exception e) {
			log.error("WebSocket 발행 실패 - userId: {}, error: {}",
				response.userId(), e.getMessage(), e);
		}
	}

	//broadcast
	public void publishBatchUpdate(WaitingQueueBatchEvent event) {

		String destination = "/topic/events/" + event.eventId() + "/queue";
		// Map<Long, WaitingQueueResponse> 전송
		//messagingTemplate.convertAndSend(destination, event.updates());

		//테스트용 로그
		try {
			messagingTemplate.convertAndSend(destination, event.updates());

			log.info("WebSocket Batch 발행 성공 - eventId: {}, 대상: {}명",
				event.eventId(), event.updates().size());

		} catch (Exception e) {
			log.error("WebSocket Batch 발행 실패 - eventId: {}, error: {}",
				event.eventId(), e.getMessage(), e);
		}
	}
}
