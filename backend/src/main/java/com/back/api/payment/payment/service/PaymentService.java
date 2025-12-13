package com.back.api.payment.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.api.payment.payment.client.PaymentClient;
import com.back.api.payment.payment.dto.request.PaymentConfirmCommand;
import com.back.api.payment.payment.dto.response.PaymentConfirmResponse;
import com.back.api.payment.payment.dto.response.PaymentConfirmResult;
import com.back.api.queue.service.QueueEntryProcessService;
import com.back.api.ticket.service.TicketService;
import com.back.domain.payment.order.entity.Order;
import com.back.domain.payment.order.repository.OrderRepository;
import com.back.domain.ticket.entity.Ticket;
import com.back.global.error.code.OrderErrorCode;
import com.back.global.error.code.PaymentErrorCode;
import com.back.global.error.exception.ErrorException;

import lombok.RequiredArgsConstructor;

/**
 * Payment 관련 비즈니스 로직 처리
 * 큐,좌석,티켓의 상태변화 과도하게 책임 -> 추후 리팩토링 필요
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentClient paymentClient;
	private final TicketService ticketService;
	private final QueueEntryProcessService queueEntryProcessService;

	@Transactional
	public PaymentConfirmResponse confirmPayment(
		Long orderId,
		String clientPaymentKey,
		Long clientAmount,
		Long userId
	) {

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new ErrorException(OrderErrorCode.ORDER_NOT_FOUND));

		// Order 소유자 검증
		if (!order.getTicket().getOwner().getId().equals(userId)) {
			throw new ErrorException(OrderErrorCode.UNAUTHORIZED_ORDER_ACCESS);
		}

		// 클라이언트가 보낸 금액과 주문 금액 일치 여부 검증
		if (!order.getAmount().equals(clientAmount)) {
			throw new ErrorException(PaymentErrorCode.AMOUNT_VERIFICATION_FAILED);
		}

		PaymentConfirmResult result = paymentClient.confirm(
			new PaymentConfirmCommand(
				order.getId(),
				order.getOrderKey(),
				order.getAmount()
			)
		);

		if (!result.success()) {
			order.markFailed();
			ticketService.failPayment(order.getTicket().getId()); // Ticket FAILED + Seat 해제
			throw new ErrorException(PaymentErrorCode.PAYMENT_FAILED);
		}

		// PG사에서 받은 paymentKey와 클라이언트가 보낸 paymentKey 일치 여부 검증
		if (!result.paymentKey().equals(clientPaymentKey)) {
			throw new ErrorException(PaymentErrorCode.PAYMENT_KEY_MISMATCH);
		}

		// Order 성공
		order.markPaid(result.paymentKey());

		// Ticket 발급
		Ticket ticket = ticketService.confirmPayment(
			order.getTicket().getId(),
			userId
		);

		// Queue 완료
		queueEntryProcessService.completePayment(
			ticket.getEvent().getId(),
			userId
		);

		return PaymentConfirmResponse.from(order, ticket);
	}
}
