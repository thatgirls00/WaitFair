package com.back.global.init.perf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.back.domain.event.entity.Event;
import com.back.domain.event.entity.EventStatus;
import com.back.domain.event.repository.EventRepository;
import com.back.domain.queue.entity.QueueEntry;
import com.back.domain.queue.repository.QueueEntryRedisRepository;
import com.back.domain.queue.repository.QueueEntryRepository;
import com.back.domain.user.entity.User;
import com.back.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("perf")
public class PerfQueueDataInitializer {

	private final QueueEntryRepository queueEntryRepository;
	private final QueueEntryRedisRepository queueEntryRedisRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;

	public void init(double queueRatio) {
		List<User> users = userRepository.findAll();
		if (users.isEmpty()) {
			log.warn("User 데이터가 없습니다. PerfUserDataInitializer를 먼저 실행해주세요.");
			return;
		}

		int event2Count = 0;
		int event3Count = 0;

		// Event #2 (QUEUE_READY): WAITING 큐 생성
		Event event2 = eventRepository.findById(2L).orElse(null);
		if (event2 == null || event2.getStatus() != EventStatus.QUEUE_READY) {
			log.warn("Event #2가 없거나 QUEUE_READY 상태가 아닙니다.");
		} else {
			// 이미 생성된 데이터 확인
			long existingCount = queueEntryRepository.countByEvent_Id(event2.getId());
			if (existingCount > 0) {
				log.info("Event #2 QueueEntry 데이터가 이미 {}건 존재합니다. 건너뜁니다.", existingCount);
			} else {
				log.info("QueueEntry 초기 데이터 생성 중: Event #2 ({}) - WAITING 큐, 비율 {}%",
					event2.getTitle(), (int) (queueRatio * 100));

				int queueCount = (int) (users.size() * queueRatio);
				List<QueueEntry> queueEntries = createQueueEntriesForEvent(event2, users, queueCount);
				queueEntryRepository.saveAll(queueEntries);

				// Redis WAITING 큐에 추가
				createRedisWaitingQueueData(event2.getId(), users, queueCount);

				event2Count = queueEntries.size();
				log.info("✅ Event #2 QueueEntry 생성 완료: {}건 (DB + Redis WAITING 큐)", event2Count);
			}
		}

		// Event #3 (OPEN): ENTERED 큐 생성
		Event event3 = eventRepository.findById(3L).orElse(null);
		if (event3 == null || event3.getStatus() != EventStatus.OPEN) {
			log.warn("Event #3이 없거나 OPEN 상태가 아닙니다.");
		} else {
			// 이미 생성된 데이터 확인
			long existingCount = queueEntryRepository.countByEvent_Id(event3.getId());
			if (existingCount > 0) {
				log.info("Event #3 QueueEntry 데이터가 이미 {}건 존재합니다. 건너뜁니다.", existingCount);
			} else {
				log.info("QueueEntry 초기 데이터 생성 중: Event #3 ({}) - ENTERED 큐, 비율 {}%",
					event3.getTitle(), (int) (queueRatio * 100));

				int queueCount = (int) (users.size() * queueRatio);
				List<QueueEntry> queueEntries = createQueueEntriesForEvent(event3, users, queueCount);
				queueEntryRepository.saveAll(queueEntries);

				// Redis ENTERED 큐에 추가 (대기열을 통과한 상태)
				createRedisEnteredQueueData(event3.getId(), users, queueCount);

				event3Count = queueEntries.size();
				log.info("✅ Event #3 QueueEntry 생성 완료: {}건 (DB + Redis ENTERED 큐)", event3Count);
			}
		}

		log.info("✅ QueueEntry 데이터 생성 완료: Event #2 (WAITING) {}건, Event #3 (ENTERED) {}건",
			event2Count, event3Count);
	}

	private List<QueueEntry> createQueueEntriesForEvent(Event event, List<User> users, int count) {
		List<QueueEntry> queueEntries = new ArrayList<>();

		int queueCount = Math.min(count, users.size());

		for (int i = 0; i < queueCount; i++) {
			User user = users.get(i);
			int rank = i + 1;

			QueueEntry queueEntry = new QueueEntry(user, event, rank);
			queueEntries.add(queueEntry);
		}

		return queueEntries;
	}

	private void createRedisWaitingQueueData(Long eventId, List<User> users, int count) {
		try {
			int queueCount = Math.min(count, users.size());

			// WAITING 큐에 사용자 추가
			for (int i = 0; i < queueCount; i++) {
				queueEntryRedisRepository.addToWaitingQueue(
					eventId,
					users.get(i).getId(),
					i + 1
				);
			}

			log.info("Redis WAITING 큐 저장 완료 - eventId: {}, count: {}", eventId, queueCount);
		} catch (Exception e) {
			log.error("Redis WAITING 큐 데이터 생성 실패: {}", e.getMessage(), e);
		}
	}

	private void createRedisEnteredQueueData(Long eventId, List<User> users, int count) {
		try {
			int queueCount = Math.min(count, users.size());

			// ENTERED 큐에 사용자 추가 (대기열을 통과한 상태)
			for (int i = 0; i < queueCount; i++) {
				queueEntryRedisRepository.addToEnteredQueueDirectly(
					eventId,
					users.get(i).getId()
				);
			}

			// ENTERED 카운트도 설정
			queueEntryRedisRepository.setEnteredCount(eventId, queueCount);

			log.info("Redis ENTERED 큐 저장 완료 - eventId: {}, count: {}", eventId, queueCount);
		} catch (Exception e) {
			log.error("Redis ENTERED 큐 데이터 생성 실패: {}", e.getMessage(), e);
		}
	}
}
