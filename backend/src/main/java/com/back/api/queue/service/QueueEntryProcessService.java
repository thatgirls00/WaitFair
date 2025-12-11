package com.back.api.queue.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.api.queue.dto.response.CompletedQueueResponse;
import com.back.api.queue.dto.response.EnteredQueueResponse;
import com.back.api.queue.dto.response.ExpiredQueueResponse;
import com.back.domain.queue.entity.QueueEntry;
import com.back.domain.queue.entity.QueueEntryStatus;
import com.back.domain.queue.repository.QueueEntryRedisRepository;
import com.back.domain.queue.repository.QueueEntryRepository;
import com.back.global.error.code.QueueEntryErrorCode;
import com.back.global.error.exception.ErrorException;
import com.back.global.event.EventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * 대기열 처리 로직
 * QueueEntry 입장 처리
 * 스케줄러, 배치 활용
 * 대기중 -> 입장 완료
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueEntryProcessService {

	private final QueueEntryRepository queueEntryRepository;
	private final QueueEntryRedisRepository queueEntryRedisRepository;
	private final EventPublisher eventPublisher;

	@Transactional
	public void processEntry(Long eventId, Long userId) {
		QueueEntry queueEntry = queueEntryRepository.findByEvent_IdAndUser_Id(eventId, userId)
			.orElseThrow(() -> new ErrorException(QueueEntryErrorCode.NOT_FOUND_QUEUE_ENTRY));

		validateEntry(queueEntry);

		queueEntry.enterQueue();
		queueEntryRepository.save(queueEntry);

		updateRedis(eventId, userId);

		publishEnteredEvent(queueEntry); // 입장 처리 웹소켓 이벤트 발행

		//TODO 입장완료 알림 로직 구현
	}

	@Transactional
	public void processBatchEntry(Long eventId, List<Long> userIds) {

		int successCount = 0;
		int failCount = 0;

		for (Long userId : userIds) {
			try {
				processEntry(eventId, userId);
				successCount++;
			} catch (ErrorException e) {
				log.error("eventId {} / userId {} 처리 중 오류 발생: {}", eventId, userId, e.getMessage());
				failCount++;
			}
		}
	}

	private void validateEntry(QueueEntry queueEntry) {
		QueueEntryStatus status = queueEntry.getQueueEntryStatus();

		//이미 입장한 경우
		if (status == QueueEntryStatus.ENTERED) {
			throw new ErrorException(QueueEntryErrorCode.ALREADY_ENTERED);
		}

		//이미 만료된 경우
		if (status == QueueEntryStatus.EXPIRED) {
			throw new ErrorException(QueueEntryErrorCode.ALREADY_EXPIRED);
		}

		//이미 결제된 경우
		if (status == QueueEntryStatus.COMPLETED) {
			throw new ErrorException(QueueEntryErrorCode.ALREADY_COMPLETED);
		}

		//대기중 상태가 아닌 경우
		if (status != QueueEntryStatus.WAITING) {
			throw new ErrorException(QueueEntryErrorCode.NOT_WAITING_STATUS);
		}
	}

	private void updateRedis(Long eventId, Long userId) {
		try {
			queueEntryRedisRepository.moveToEnteredQueue(eventId, userId);
			queueEntryRedisRepository.incrementEnteredCount(eventId);
			log.debug("eventId {} - Redis 업데이트 성공", eventId);

		} catch (Exception e) {
			log.error("eventId {} - Redis 업데이트 실패", eventId);
		}
	}

	private void publishEnteredEvent(QueueEntry queueEntry) {
		EnteredQueueResponse response = EnteredQueueResponse.from (
			queueEntry.getUserId(),
			queueEntry.getEventId(),
			queueEntry.getEnteredAt(),
			queueEntry.getExpiredAt()
		);

		eventPublisher.publishEvent(response);
	}


	public boolean canEnterEntry(Long eventId, Long userId) {
		return queueEntryRepository
			.findByEvent_IdAndUser_Id(eventId, userId)
			.map(entry -> entry.getQueueEntryStatus() == QueueEntryStatus.WAITING)
			.orElse(false);
	}

	@Transactional
	public void expireEntry(Long eventId, Long userId) {
		QueueEntry queueEntry = queueEntryRepository.findByEvent_IdAndUser_Id(eventId, userId)
			.orElseThrow(() -> new ErrorException(QueueEntryErrorCode.NOT_FOUND_QUEUE_ENTRY));

		if (queueEntry.getQueueEntryStatus() == QueueEntryStatus.EXPIRED) {
			return;
		}

		if (queueEntry.getQueueEntryStatus() != QueueEntryStatus.ENTERED) {
			return;
		}

		queueEntry.expire();
		queueEntryRepository.save(queueEntry);

		try {
			queueEntryRedisRepository.removeFromEnteredQueue(eventId, userId);
			log.debug("eventId {} - Redis 만료 처리 성공", eventId);
		} catch (Exception e) {
			log.error("eventId {} - Redis 만료 처리 실패", eventId);
		}

		publishExpiredEvent(queueEntry);  // 만료 처리 웹소켓 이벤트 발행

		//TODO 알림 로직 구현 필요
	}

	@Transactional
	public void expireBatchEntries(List<QueueEntry> entries) {

		int successCount = 0;

		for (QueueEntry entry : entries) {
			expireEntry(entry.getEventId(), entry.getUserId());
			successCount++;
		}
		log.info("총 {}개 대기열 항목 만료 처리 완료", successCount);
	}

	private void publishExpiredEvent(QueueEntry queueEntry) {
		ExpiredQueueResponse response = ExpiredQueueResponse.from(
			queueEntry.getUserId(),
			queueEntry.getEventId()
		);

		eventPublisher.publishEvent(response);
	}

	//TODO 결제 도메인에서 사용 필요
	@Transactional
	public void completePayment(Long eventId, Long userId) {

		QueueEntry queueEntry = queueEntryRepository.findByEvent_IdAndUser_Id(eventId, userId)
			.orElseThrow(() -> new ErrorException(QueueEntryErrorCode.NOT_FOUND_QUEUE_ENTRY));

		validatePaymentCompletion(queueEntry);

		queueEntry.completePayment();
		queueEntryRepository.save(queueEntry);

		try {
			queueEntryRedisRepository.removeFromEnteredQueue(eventId, userId);
		} catch (Exception e) {
			log.error("결제 완료 사용자 대기열 제거 실패");
		}

		publishCompletedEvent(queueEntry); // 결제 완료 처리 웹소켓 이벤트 발행

	}

	private void validatePaymentCompletion(QueueEntry queueEntry) {
		QueueEntryStatus status = queueEntry.getQueueEntryStatus();

		// 이미 결제 완료된 경우
		if (status == QueueEntryStatus.COMPLETED) {
			throw new ErrorException(QueueEntryErrorCode.ALREADY_COMPLETED);
		}

		// 만료된 경우
		if (status == QueueEntryStatus.EXPIRED) {
			throw new ErrorException(QueueEntryErrorCode.ALREADY_EXPIRED);
		}

		// 대기 중인 경우 (입장도 안 했는데 결제 시도)
		if (status == QueueEntryStatus.WAITING) {
			throw new ErrorException(QueueEntryErrorCode.NOT_ENTERED_STATUS);
		}

		// ENTERED 상태가 아닌 경우
		if (status != QueueEntryStatus.ENTERED) {
			throw new ErrorException(QueueEntryErrorCode.NOT_ENTERED_STATUS);
		}
	}

	private void publishCompletedEvent(QueueEntry queueEntry) {
		CompletedQueueResponse response = CompletedQueueResponse.from(
			queueEntry.getUserId(),
			queueEntry.getEventId()
		);

		eventPublisher.publishEvent(response);

	}


}
