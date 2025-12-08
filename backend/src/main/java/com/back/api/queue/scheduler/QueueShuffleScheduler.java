package com.back.api.queue.scheduler;

import org.springframework.stereotype.Component;

import com.back.api.queue.service.QueueShuffleService;
import com.back.domain.event.repository.EventRepository;
import com.back.domain.queue.repository.QueueEntryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueShuffleScheduler {

	private final QueueEntryRepository queueEntryRepository;
	private final EventRepository eventRepository; //TODO service로 변경 필요
	private final QueueShuffleService queueShuffleService;
}
