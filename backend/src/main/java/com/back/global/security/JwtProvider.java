package com.back.global.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.back.domain.user.entity.User;
import com.back.global.utils.JwtUtil;

import lombok.Getter;

@Component
@Getter
public class JwtProvider {

	@Value("${custom.jwt.secret}")
	private String secret;

	@Value("${custom.jwt.access-token-duration}")
	private long accessTokenDurationMillis;

	@Value("${custom.jwt.refresh-token-duration}")
	private long refreshTokenDurationMillis;

	public String generateAccessToken(User user) {
		return JwtUtil.toString(
			secret,
			accessTokenDurationMillis,
			Map.of("id", user.getId(), "nickname", user.getNickname(), "role", user.getRole())
		);
	}

	public String generateRefreshToken(User user) {
		return JwtUtil.toString(
			secret,
			refreshTokenDurationMillis,
			Map.of("id", user.getId(), "nickname", user.getNickname(), "role", user.getRole())
		);
	}

	/** access token 유효 기간 (ms 단위) */
	public long getAccessTokenValidityMillis() {
		return accessTokenDurationMillis;
	}

	/** refresh token 유효 기간 (ms 단위) */
	public long getRefreshTokenValidityMillis() {
		return refreshTokenDurationMillis;
	}

	/** access token 만료 시각 (epoch milli) */
	public long getAccessTokenExpiresAtMillis() {
		return System.currentTimeMillis() + accessTokenDurationMillis;
	}

	/** refresh token 만료 시각 (epoch milli) */
	public long getRefreshTokenExpiresAtMillis() {
		return System.currentTimeMillis() + refreshTokenDurationMillis;
	}
}
