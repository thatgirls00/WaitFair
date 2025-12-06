package com.back.api.seat.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record BulkCreateSeatsRequest(
	@Schema(description = "생성할 좌석 목록")
	List<SeatCreateRequest> seats
) {
}
