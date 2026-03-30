# 문서 가이드

이 문서는 프로젝트를 처음 접하는 분들을 위한 문서 허브입니다.
아래 추천 읽기 순서에 따라 프로젝트를 파악하세요.

---

## 처음이라면 이 순서로 읽어보세요

| 순서 | 문서 | 내용 |
|------|------|------|
| 1 | [ERD](./architecture/ERD.md) | 전체 데이터 모델 한눈에 파악 |
| 2 | [API 명세](./architecture/API_SPEC.md) | 제공하는 API 엔드포인트와 요청/응답 구조 |
| 3 | [시퀀스 다이어그램](./architecture/SEQUANCE_DIAGRAM.md) | 주요 비즈니스 플로우 (잔액, 상품, 주문, 결제) |
| 3-1 | [주문-결제 상세 플로우](./architecture/ORDER_PAYMENT_FLOW.md) | 주문-결제 전체 서비스 플로우 (Kafka, Saga, 보상 트랜잭션 포함) |
| 4 | [이벤트 아키텍처](./architecture/event.md) | 도메인 간 이벤트 기반 통신 구조 |
| 5 | [Kafka 구성](./infra/kafka.md) | Kafka 설정 및 아웃박스 패턴 |

---

## 주제별 심화 문서

### 동시성 제어
- [락 전략 분석](./concurrency/LOCK_REPORT.md) — 비관적/낙관적 락과 Redis 분산락 선택 근거
- [주문 동시성](./concurrency/order.md) — 재고 차감 시 동시성 제어 구현
- [결제 동시성](./concurrency/payment.md) — 잔액 차감 시 동시성 제어 구현

### 성능 최적화
- [캐싱 전략](./performance/cache.md) — AOP 기반 커스텀 Redis 캐시, TTL 설계 및 성능 비교
- [DB 인덱스 전략](./performance/index.md) — 쿼리 성능 개선을 위한 인덱스 설계 및 전후 비교
- [성능 테스트 시나리오](./performance/perf.md) — k6 부하 테스트 시나리오 설계
- [부하 테스트 결과](./performance/load_test.md) — 병목 구간 분석 및 개선 방향

### 인프라 & 운영
- [Kafka 구성](./infra/kafka.md) — Docker Compose Kafka 설정 및 연동 테스트
- [모니터링 & 장애 대응](./infra/monitoring.md) — Prometheus + Grafana 구성, OOM 장애 분석

### 프로젝트 기록
- [마일스톤](./planning/MILESTONE.md) — 주차별 작업 계획 및 진행 현황
- [개선 구현 계획](./planning/COMMERCE_IMPROVEMENT_PLAN.md) — P0~P3 우선순위별 기능 개선 현황
- [기술 개선 사항](./planning/TECHNICAL_IMPROVEMENTS.md) — 코드 리뷰에서 발견된 기술적 이슈 및 수정 내용
- [챕터 회고](./planning/CHAPTER_REVIEW.md) — 프로젝트 진행 과정에서의 학습과 회고

---

## API 테스트

- [HTTP 요청 샘플](./http/) — IntelliJ HTTP Client용 주문/결제 요청 예시
- [Postman 컬렉션](./postman/postman.json) — Postman으로 바로 import 가능한 컬렉션
