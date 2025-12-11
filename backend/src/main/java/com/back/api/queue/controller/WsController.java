package com.back.api.queue.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WsController {

	private final SimpMessagingTemplate messagingTemplate;

	// @GetMapping("/ws-test")
	// public void test() {
	// 	WaitingQueueResponse res =
	// 		new WaitingQueueResponse(2L, 4L, QueueEntryStatus.WAITING, 1, 0, 90);
	// 	messagingTemplate.convertAndSend("/topic/users/2/queue", res);
	// }
}
