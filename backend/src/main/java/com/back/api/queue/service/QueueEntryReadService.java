package com.back.api.queue.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.domain.queue.repository.QueueEntryRedisRepository;
import com.back.domain.queue.repository.QueueEntryRepository;

import lombok.RequiredArgsConstructor;

/*
 * 큐 대기열 조회 서비스
 * Redis 우선 조회 -> DB 조회
 * 트랜잭션 읽기 / 쓰기 분리 고려

 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueueEntryReadService {

	private final QueueEntryRepository queueEntryRepository;
	private final QueueEntryRedisRepository queueEntryRedisRepository;
}
