package com.back.api.queue.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.back.api.queue.dto.event.WaitingQueueBatchEvent;
import com.back.api.queue.dto.response.QueueEntryStatusResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO 로그 제거
public class QueueEntryEventHandler {

	private final QueueEntryWebSocketPublisher publisher;

	//Spring ApplicationEvent 수신
	//@EventListenr가 QueueEntryStatusResponse 타입의 이벤트 감지
	@EventListener
	@Async
	public void handleQueueStatus(QueueEntryStatusResponse response) {

		//publisher.publisherToUser(response);

		//테스트용 로그
		log.info("대기열 이벤트 수신 - userId: {}, eventId: {}, type: {}",
			response.userId(), response.eventId(), response.getClass().getSimpleName());

		try {
			publisher.publisherToUser(response);
		} catch (Exception e) {
			log.error("대기열 이벤트 처리 실패 - userId: {}, error: {}",
				response.userId(), e.getMessage(), e);
		}
	}

	@EventListener
	@Async
	public void handleQueueBatchUpdate(WaitingQueueBatchEvent event) {

		//publisher.publishBatchUpdate(event);

		//테스트용 로그
		log.info("대기열 Batch 이벤트 수신 - eventId: {}, 대상: {}명",
			event.eventId(), event.updates().size());

		try {
			publisher.publishBatchUpdate(event);
		} catch (Exception e) {
			log.error("대기열 Batch 이벤트 처리 실패 - eventId: {}, error: {}",
				event.eventId(), e.getMessage(), e);
		}
	}

}
