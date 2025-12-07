package com.back.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.info(new Info()
				.title("WaitFair API")
				.description("사전 추첨과 강화된 보안을 갖춘 차세대 스마트 예매 플랫폼")
				.version("v1.0.0"));
	}
}
