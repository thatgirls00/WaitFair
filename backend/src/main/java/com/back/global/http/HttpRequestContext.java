package com.back.global.http;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.back.domain.user.entity.User;
import com.back.domain.user.repository.UserRepository;
import com.back.global.error.code.AuthErrorCode;
import com.back.global.error.exception.ErrorException;
import com.back.global.security.SecurityUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HttpRequestContext {

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final UserRepository userRepository;

	@Value("${custom.site.domain:localhost}")
	private String domain;

	@Value("${custom.jwt.access-token-duration}")
	private long accessTokenDurationMillis;

	@Value("${custom.jwt.refresh-token-duration}")
	private long refreshTokenDurationMillis;

	private Authentication getAuthentication() {
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
			.orElseThrow(() -> new ErrorException(AuthErrorCode.UNAUTHORIZED));
	}

	public SecurityUser getSecurityUser() {
		return Optional.of(getAuthentication())
			.map(Authentication::getPrincipal)
			.filter(principal -> principal instanceof SecurityUser)
			.map(principal -> (SecurityUser)principal)
			.orElseThrow(() -> new ErrorException(AuthErrorCode.UNAUTHORIZED));
	}

	public User getUser() {
		SecurityUser securityUser = getSecurityUser();

		return userRepository.findById(securityUser.getId())
			.orElseThrow(() -> new ErrorException(AuthErrorCode.UNAUTHORIZED));
	}

	public String getUserAgent() {
		String userAgent = request.getHeader("User-Agent");
		return userAgent != null ? userAgent : "unknown";
	}

	public String getClientIp() {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
			return ip.split(",")[0].trim();
		}

		ip = request.getHeader("X-Real-IP");
		if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}

		return request.getRemoteAddr();
	}
}
