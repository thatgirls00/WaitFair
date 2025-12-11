package com.back.api.queue.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.back.api.queue.dto.response.QueueEntryStatusResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueEntryWebSocketPublisher {

	private final SimpMessagingTemplate messagingTemplate;

	//사용자에게 WebSocket 메시지 전송
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
}
