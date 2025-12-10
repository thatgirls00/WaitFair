package com.back.api.payment.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.back.api.payment.order.dto.request.OrderRequestDto;
import com.back.api.payment.order.dto.response.OrderResponseDto;
import com.back.api.payment.order.service.OrderService;
import com.back.domain.payment.order.entity.Order;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;


	@PostMapping("/api/v1/order")
	public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
		Order order = orderService.createOrder(orderRequestDto);
		OrderResponseDto orderResponseDto = OrderResponseDto.toDto(order);
		return ResponseEntity.ok(orderResponseDto);
	}

}
