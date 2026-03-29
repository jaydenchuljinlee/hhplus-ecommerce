# CLAUDE.md

## 빌드 & 테스트 명령어

```bash
# 빌드
./gradlew build

# 전체 테스트
./gradlew test

# 단일 테스트 클래스 (성능상 이유로 전체보다 단일 테스트 선호)
./gradlew test --tests "TestClassName"

# 특정 테스트 메서드
./gradlew test --tests "TestClassName.methodName"

# 애플리케이션 실행
./gradlew bootRun
```

## 코드 스타일

- Kotlin 사용, Java 17 타겟
- 인터페이스: `I{Name}` 접두사 (예: `IOrderRepository`, `IProductController`)
- Repository 구현체는 인프라 계층에 위치
- 도메인 서비스는 인프라 의존성 없이 순수하게 유지

## 아키텍처 규칙

- **패키지 구조**: 도메인별 분리 → `api/` → `domain/` → `infrastructure/` → `usecase/`
- **의존성 방향**: api → usecase → domain ← infrastructure (도메인이 인프라에 의존하지 않음)
- Facade/UseCase 계층에서 여러 서비스를 오케스트레이션
- 도메인 이벤트로 도메인 간 느슨한 결합 유지 (Spring ApplicationEventPublisher)
- Kafka 아웃박스 패턴으로 이벤트 신뢰성 보장 (OutboxEvent)
- 분산락은 `@RedisLock` 커스텀 애너테이션 사용

## 테스트 규칙

- 통합 테스트는 TestContainers 사용 (MySQL, Kafka, Redis)
- 테스트 설정: `application-test.yml`
- 동시성 테스트는 별도 테스트 클래스로 분리


## 검증

- 일련의 코드 변경을 완료했을 때 `./gradlew test --tests` 로 관련 테스트를 실행하여 검증한다
- 빌드 실패 시 근본 원인을 해결하고, 오류를 억제하지 않는다

## 에이전틱 워크플로우

### 기능 개발 파이프라인
- 기능을 추가하고, 커밋하기 전에는 항상 테스트를 수행
- 테스트가 실패할 때는 단순히 Mock처리를 해서 성공하게끔 만드는 것이 아니라, 로직에서의 개선을 통해 테스트가 성공하도록 수행

### 코드 리뷰
- `"subagent를 사용하여 코드를 리뷰해줘"` → @code-reviewer 호출

### 스킬 (컨벤션 참조)
- `/api-endpoint` — 새 API 엔드포인트 생성 시 컨벤션 가이드
- `/domain-service` — 새 도메인 서비스/Repository/Facade 생성 시 컨벤션 가이드
- `/kafka-event` — Kafka 이벤트/Outbox 패턴 구현 시 컨벤션 가이드

## 압축 시 보존 규칙

When compacting, always preserve: 수정된 파일 전체 목록, 실행한 테스트 명령어, 실패한 테스트 결과