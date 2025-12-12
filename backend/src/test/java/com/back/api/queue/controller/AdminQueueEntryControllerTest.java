package com.back.api.queue.controller;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.back.support.helper.QueueEntryHelper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@DisplayName("AdminQueueEntryController 통합 테스트")
public class AdminQueueEntryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private QueueEntryHelper queueEntryHelper;

}
