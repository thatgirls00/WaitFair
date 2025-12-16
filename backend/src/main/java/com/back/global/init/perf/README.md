# 부하테스트용 데이터 초기화

perf 프로필 실행 시 자동으로 부하테스트용 초기 데이터를 생성합니다.

## 실행 방법

### 기본 실행
```bash
./gradlew bootRun
```

**기본값**: users=300, events=20, prereg-ratio=1.0, queue-ratio=1.0, ticket-ratio=1.0

### 커스텀 파라미터 실행
```bash
./gradlew bootRun --args='--users=1000 --events=20 --prereg-ratio=0.9 --queue-ratio=0.8'
```

**파라미터**:
- `--users`: 생성할 사용자 수
- `--events`: 생성할 이벤트 수 (최소 4개)
- `--prereg-ratio`: 사전등록 비율 (0.0 ~ 1.0)
- `--queue-ratio`: 대기열 진입 비율 (0.0 ~ 1.0)
- `--ticket-ratio`: 티켓 발급 비율 (0.0 ~ 1.0, 현재 미사용)

---

## 부하테스트 타겟 이벤트

### Event #1 (아이유 콘서트) - PRE_OPEN
- **테스트 목적**: 사전등록 API 동시 호출
- **데이터**: PreRegister ~300건

### Event #2 (세븐틴 콘서트) - QUEUE_READY
- **테스트 목적**: 대기열 진입 및 WAITING 큐 순위 조회
- **데이터**: QueueEntry ~300건 (DB + Redis WAITING 큐)

### Event #3 (뉴진스 콘서트) - OPEN
- **테스트 목적**: 500석 경쟁 티켓팅 부하테스트
- **데이터**:
  - Seat: 500석 (VIP-A 50, R-C 100, S-B 150, A-D 200)
  - QueueEntry: ~300건 (DB + Redis ENTERED 큐)

### Event #4 (르세라핌 콘서트) - CLOSED
- **테스트 목적**: 티켓 조회/관리 READ 작업
- **데이터**:
  - Seat: 100석 (VIP-E 20, R-G 30, S-F 30, A-H 20) → 모두 SOLD
  - Ticket: 100장 → 모두 ISSUED

---

## 총 데이터 규모 (기본값: users=300)

- **User**: 301명 (테스트 300명 + 관리자 1명)
- **Event**: 20개 (필수 4개 + 더미 16개)
- **Seat**: 600석 (Event #3: 500석, Event #4: 100석)
- **PreRegister**: ~300건 (Event #1)
- **QueueEntry**: ~600건 (Event #2 WAITING 300건, Event #3 ENTERED 300건)
- **Ticket**: 100장 (Event #4, 모두 ISSUED)

---

## 중요 사항

### 1. Redis 필수
QueueEntry 생성 시 Redis를 사용합니다:
```bash
docker start redis
```

### 2. DB 자동 초기화
perf 프로필은 `ddl-auto: create` 설정으로 **앱 시작 시마다 DB가 초기화**됩니다.

### 3. 데이터 재생성
Redis 초기화 후 재실행:
```bash
docker exec redis redis-cli FLUSHALL
./gradlew bootRun
```

### 4. 관리자 계정
- Email: `admin@test.com`
- Password: `admin1234`
