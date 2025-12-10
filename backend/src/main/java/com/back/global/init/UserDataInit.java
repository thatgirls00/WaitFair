package com.back.global.init;

import java.time.LocalDate;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.back.domain.user.entity.User;
import com.back.domain.user.entity.UserActiveStatus;
import com.back.domain.user.entity.UserRole;
import com.back.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class UserDataInit implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) {
		if (userRepository.count() > 0) {
			log.info("User 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
			return;
		}

		log.info("User 초기 데이터를 생성합니다.");

		// 사전등록 테스트용 유저
		User testUser = User.builder()
			.email("test@example.com")
			.nickname("홍길동")
			.password(passwordEncoder.encode("password123"))
			.birthDate(LocalDate.of(1990, 1, 1))
			.role(UserRole.NORMAL)
			.activeStatus(UserActiveStatus.ACTIVE)
			.build();

		userRepository.save(testUser);

		log.info("User 초기 데이터 1개가 생성되었습니다. (nickname: 홍길동, birthDate: 1990-01-01)");
	}
}
