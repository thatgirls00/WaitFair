package com.back.api.auth.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.back.domain.user.entity.User;
import com.back.domain.user.entity.UserRole;
import com.back.domain.user.repository.UserRepository;
import com.back.global.error.code.AuthErrorCode;
import com.back.support.data.TestUser;
import com.back.support.factory.UserFactory;
import com.back.support.helper.UserHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserHelper userHelper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${custom.jwt.secret}")
	private String secret;

	private final ObjectMapper mapper = new ObjectMapper();
	private TestUser testUser;
	private User user;

	@BeforeEach
	void setUp() {
		testUser = UserFactory.fakeUser(UserRole.NORMAL, passwordEncoder);
		user = testUser.user();
	}

	@Nested
	@DisplayName("POST `/api/v1/auth/signup`")
	class SignupTest {

		private final String signUpApi = "/api/v1/auth/signup";

		@Test
		@DisplayName("Success Sign up")
		void signup_success() throws Exception {

			String requestJson = mapper.writeValueAsString(Map.of(
				"email", user.getEmail(),
				"password", testUser.rawPassword(),
				"nickname", user.getNickname(),
				"year", "2002",
				"month", "2",
				"day", "11"
			));

			ResultActions actions = mvc
				.perform(
					post(signUpApi)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
				).andDo(print());

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("signup"))
				.andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
				.andExpect(jsonPath("$.message").value("회원가입 성공"));

			User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow(
				() -> new RuntimeException("Not found user")
			);

			assertThat(savedUser.getNickname()).isEqualTo(user.getNickname());
		}

		@Test
		@DisplayName("Failed sign up by missing params")
		void signup_failed_by_missing_params() throws Exception {

			String requestJson = mapper.writeValueAsString(Map.of(
				"email", user.getEmail(),
				"password", user.getPassword(),
				"year", "2002",
				"month", "2",
				"day", "11"
			));

			ResultActions actions = mvc
				.perform(
					post(signUpApi)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
				).andDo(print());

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("signup"))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Failed sign up by existing email")
		void signup_failed_by_existing_email() throws Exception {

			TestUser existedUser = userHelper.createUser(UserRole.NORMAL);

			String requestJson = mapper.writeValueAsString(Map.of(
				"email", existedUser.user().getEmail(),
				"password", existedUser.rawPassword(),
				"nickname", "A" + existedUser.user().getNickname(),
				"year", "2002",
				"month", "2",
				"day", "11"
			));

			ResultActions actions = mvc
				.perform(
					post(signUpApi)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
				).andDo(print());

			AuthErrorCode error = AuthErrorCode.ALREADY_EXIST_EMAIL;

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("signup"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(error.getMessage()));
		}

		@Test
		@DisplayName("Failed sign up by existing nickname")
		void signup_failed_by_existing_nickname() throws Exception {

			TestUser existedUser = userHelper.createUser(UserRole.NORMAL);

			String requestJson = mapper.writeValueAsString(Map.of(
				"email", "test" + existedUser.user().getEmail(),
				"password", existedUser.rawPassword(),
				"nickname", existedUser.user().getNickname(),
				"year", "2002",
				"month", "2",
				"day", "11"
			));

			ResultActions actions = mvc
				.perform(
					post(signUpApi)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
				).andDo(print());

			AuthErrorCode error = AuthErrorCode.ALREADY_EXIST_NICKNAME;

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("signup"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(error.getMessage()));
		}
	}

	@Nested
	@DisplayName("POST `/api/v1/auth/login`")
	class LoginTest {

		private final String loginApi = "/api/v1/auth/login";

		@Test
		@DisplayName("Success login")
		void login_success() throws Exception {
			TestUser existedUser = userHelper.createUser(UserRole.NORMAL);
			User savedUser = existedUser.user();
			String rawPassword = existedUser.rawPassword();

			String requestJson = mapper.writeValueAsString(Map.of(
				"email", savedUser.getEmail(),
				"password", rawPassword
			));

			ResultActions actions = mvc.perform(
				post(loginApi)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
			).andDo(print());

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("login"))
				.andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
				.andExpect(jsonPath("$.message").value("로그인 성공"));
		}

		@Test
		@DisplayName("Failed by missing password parameter")
		void failed_by_missing_password_parameter() throws Exception {
			TestUser existedUser = userHelper.createUser(UserRole.NORMAL);
			User savedUser = existedUser.user();

			String requestJson = mapper.writeValueAsString(Map.of(
				"email", savedUser.getEmail()
			));

			ResultActions actions = mvc.perform(
				post(loginApi)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
			).andDo(print());

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("login"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."));
		}

		@Test
		@DisplayName("Failed by missing email parameter")
		void failed_by_missing_email_parameter() throws Exception {
			TestUser existedUser = userHelper.createUser(UserRole.NORMAL);
			String rawPassword = existedUser.rawPassword();

			String requestJson = mapper.writeValueAsString(Map.of(
				"password", rawPassword
			));

			ResultActions actions = mvc.perform(
				post(loginApi)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
			).andDo(print());

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("login"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("이메일을 입력해주세요."));
		}

		@Test
		@DisplayName("Failed by wrong email parameter")
		void failed_by_wrong_email_parameter() throws Exception {
			TestUser existedUser = userHelper.createUser(UserRole.NORMAL);
			User savedUser = existedUser.user();
			String rawPassword = existedUser.rawPassword();

			String requestJson = mapper.writeValueAsString(Map.of(
				"email", "A" + savedUser.getEmail(),
				"password", rawPassword
			));

			ResultActions actions = mvc.perform(
				post(loginApi)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
			).andDo(print());

			AuthErrorCode error = AuthErrorCode.LOGIN_FAILED;

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("login"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(error.getMessage()));
		}

		@Test
		@DisplayName("Failed by wrong password parameter")
		void failed_by_wrong_password_parameter() throws Exception {
			TestUser existedUser = userHelper.createUser(UserRole.NORMAL);
			User savedUser = existedUser.user();
			String rawPassword = existedUser.rawPassword();

			String requestJson = mapper.writeValueAsString(Map.of(
				"email", savedUser.getEmail(),
				"password", "A" + rawPassword
			));

			ResultActions actions = mvc.perform(
				post(loginApi)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
			).andDo(print());

			AuthErrorCode error = AuthErrorCode.LOGIN_FAILED;

			actions
				.andExpect(handler().handlerType(AuthController.class))
				.andExpect(handler().methodName("login"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(error.getMessage()));
		}
	}
}
