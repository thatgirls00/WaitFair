package com.back.api.queue.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "QueueEntry Admin API", description = "관리자 대기열 API")
@RestController
@RequestMapping("/api/v1/admin/queues")
@RequiredArgsConstructor
public class AdminQueueEntryController {
}
