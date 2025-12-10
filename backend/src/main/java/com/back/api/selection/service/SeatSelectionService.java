package com.back.api.selection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.api.seat.service.SeatService;
import com.back.api.ticket.service.TicketService;
import com.back.domain.queue.repository.QueueEntryRedisRepository;
import com.back.domain.seat.entity.Seat;
import com.back.domain.ticket.entity.Ticket;
import com.back.global.error.code.SeatErrorCode;
import com.back.global.error.exception.ErrorException;

import lombok.RequiredArgsConstructor;

/**
 * 좌석 선택 + DraftTicket 생성 서비스
 */
@Service
@RequiredArgsConstructor
public class SeatSelectionService {

	private final SeatService seatService;
	private final TicketService ticketService;
	private final QueueEntryRedisRepository queueEntryRedisRepository;

	/**
	 * 좌석 선택 + DraftTicket 생성
	 */
	@Transactional
	public Ticket selectSeatAndCreateTicket(Long eventId, Long seatId, Long userId) {
		validateQueueEntry(eventId, userId);

		Seat reservedSeat = seatService.reserveSeat(eventId, seatId, userId);

		Ticket draftTicket = ticketService.createDraftTicket(eventId, seatId, userId);

		return draftTicket;
	}

	// TODO : repository가 아닌 service 메소드로 QUEUE 검증
	private void validateQueueEntry(Long eventId, Long userId) {
		if (!queueEntryRedisRepository.isInEnteredQueue(eventId, userId)) {
			throw new ErrorException(SeatErrorCode.NOT_IN_QUEUE);
		}
	}
}
