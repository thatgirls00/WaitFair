package com.back.api.ticket.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.api.ticket.dto.response.TicketResponse;
import com.back.api.ticket.service.TicketService;
import com.back.domain.ticket.entity.Ticket;
import com.back.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket API", description = "티켓 결제 및 조회 API")
public class TicketController {

	private final TicketService ticketService;

	/**
	 * 결제 성공 처리
	 */
	@PostMapping("/{ticketId}/payment/success")
	@Operation(summary = "결제 성공", description = "Draft Ticket을 ISSUED 상태로 변경합니다.")
	public ApiResponse<TicketResponse> confirmPayment(
		@PathVariable Long ticketId
	) {
		Long userId = 1L;

		Ticket ticket = ticketService.confirmPayment(ticketId, userId);

		return ApiResponse.ok(
			"결제가 완료되었습니다.",
			TicketResponse.from(ticket)
		);
	}

	/**
	 * 결제 실패 처리
	 */
	@PostMapping("/{ticketId}/payment/fail")
	@Operation(summary = "결제 실패", description = "Draft Ticket을 FAILED로 변경하고 좌석을 AVAILABLE로 복구합니다.")
	public ApiResponse<Void> failPayment(
		@PathVariable Long ticketId
	) {
		ticketService.failPayment(ticketId);
		return ApiResponse.noContent("결제 실패 처리 완료");
	}

	/**
	 * 내 티켓 조회
	 */
	@GetMapping("/my")
	@Operation(summary = "내 티켓 목록 조회")
	public ApiResponse<List<TicketResponse>> getMyTickets() {
		Long userId = 1L;

		List<Ticket> tickets = ticketService.getMyTickets(userId);

		return ApiResponse.ok(
			"사용자의 티켓 목록 조회 성공",
			tickets.stream().map(TicketResponse::from).toList()
		);
	}
}