import { login } from "../scenarios/login.js";
import { sleep } from "k6";

export const options = {
  stages: [
    { duration: "10s", target: parseInt(__ENV.RAMP_UP_VUS || "50", 10) },   // 10초 동안 50명으로 증가
    { duration: "1m", target: parseInt(__ENV.RAMP_UP_VUS || "50", 10) },     // 1분간 50명 유지
    { duration: "10s", target: parseInt(__ENV.PEAK_VUS || "100", 10) },      // 10초 동안 100명으로 증가 (피크)
    { duration: "1m", target: parseInt(__ENV.PEAK_VUS || "100", 10) },       // 1분간 100명 유지
    { duration: "1m", target: 0 },                                           // 1분 동안 0으로 감소
  ],
};

/**
 * Perf 더미데이터 규칙: PerfDataInitializer
 * - email: test{i+1}@test.com
 * - password: abc12345 <- 통일
 *
 * 기존 부하테스트와 동일하게 VU 기반으로 사용자 선택
 */
export function setup() {
  const users = Array.from({ length: 500 }, (_, i) => ({
    email: `test${i + 1}@test.com`,
    password: `abc12345`,
  }));

  return {
    users,
    testId: new Date().toISOString().replace(/[:.]/g, "-"),
  };
}

export default function (data) {
  const baseUrl = __ENV.BASE_URL || "http://host.docker.internal:8080";

  // 1~500 사이 로그인 계정 라운드로빈
  const idx = ((__VU - 1) % 500);
  const { email, password } = data.users[idx];

  login(baseUrl, data.testId, email, password);
  sleep(1); // 1초 대기
}