package com.back.global.init.perf;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Profile("perf")
@RequiredArgsConstructor
@Order(0)
public class PerfBootstrapRunner implements ApplicationRunner {

	private final PerfUserDataInitializer userInit;
	private final PerfEventDataInitializer eventInit;
	private final PerfPreregisterDataInitializer preregisterInit;
	private final PerfQueueDataInitializer queueInit;
	private final PerfSeatDataInitializer seatInit;
	private final PerfTicketDataInitializer ticketInit;

	@Override
	public void run(ApplicationArguments args) {
		// 순서만 책임
	}
}
