package com.back.api.payment.order.dto.request;

public record OrderRequestDto(
	Long amount,
	Long eventId,
	Long userId,
	Long seatId
) { }
