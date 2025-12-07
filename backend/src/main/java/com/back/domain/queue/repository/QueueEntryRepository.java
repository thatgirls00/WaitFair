package com.back.domain.queue.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.domain.queue.entity.QueueEntry;
import com.back.domain.queue.entity.QueueEntryStatus;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

	long countByEventId(Long eventId);
	long countByEventIdAndQueueEntryStatus(Long eventId, QueueEntryStatus status);
	Optional<QueueEntry> findByEventIdAndUserId(Long eventId, Long userId);

	long countWaitingAhead(Long eventId, Integer queueRank);

	boolean existsByEventIdAndUserId(Long eventId, Long userId);


}
