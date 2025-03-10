## 장애 대응 시나리오: 사용자 주문 급증으로 인한 OOM 장애 대응

---

## 장애 대응 보고서

### 장애 개요
- **장애 해결 타임라인**:
    - **장애 발생 시각**: 2024년 11월 25일 오전 10:15
    - **MTTD (Mean Time to Detect)**: 15분 (10:30 탐지 완료)
    - **MTTR (Mean Time to Resolve)**: 90분 (12:00 완전 복구)

- **장애 발생 원인**:
    - 갑작스러운 피크 트래픽(초당 5000 RPS 이상)으로 인해 주문 API 서버의 메모리 사용량이 급격히 증가.
    - JVM 힙 메모리 제한(`-Xmx256m`) 초과로 인해 **OutOfMemoryError(OOM)** 발생.
    - OOM으로 인해 애플리케이션이 강제 종료되고 요청 처리가 불가능한 상태로 전환.
    - 자동 스케일링 설정 부재와 비효율적인 리소스 관리로 인해 장애 발생.

---

### 장애 임팩트
- **주요 장애 증상**:
    - **주문 실패율** 증가: 약 40%의 주문 요청 실패.
    - 일부 주문 데이터 누락 또는 중복 저장 발생.
    - 주문 API 응답 시간이 2초 이상으로 증가.
    - 사용자 불만 접수: 120건.
    - 주문 상태가 `Processing` 상태에서 멈춤.

- **영향을 받은 시스템**:
    - **주문 요청 API 서버**:
        - OOM으로 인해 요청을 더 이상 처리하지 못하고 서버가 중단.
    - **데이터베이스 서버**:
        - 대량의 요청으로 인해 커넥션 풀이 고갈되고 응답 시간이 급격히 증가.

- **추가 영향**:
    - 다른 마이크로서비스(상품 재고, 결제 API 등)가 주문 상태 업데이트를 받지 못함.

---

### 장애 원인 분석
1. **주문 API 서버**:
    - JVM 힙 메모리 부족 (`OutOfMemoryError`)로 애플리케이션이 중단.
    - 비효율적인 메모리 관리(대량 객체 생성 및 GC 부하 증가).
2. **트래픽 관리 부족**:
    - 피크 트래픽에 대비한 부하 분산 및 스케일링 정책 부재.
3. **스레드 풀 고갈**:
    - 요청 수 증가로 인해 스레드 풀이 한계치에 도달.

---

### 대응 조치

#### 1. 장애 발생 직후 대응 조치 (2024년 11월 25일 오전 10:30)
- **주문 API 서버 재시작**:
    - OOM 발생한 서버를 재시작하여 서비스 복구.
- **트래픽 제한**:
    - 초당 요청 수 제한.
- **임시 서버 증설**:
    - 동일한 애플리케이션 서버를 수동으로 2대 추가 배포.

#### 2. 데이터 정합성 복구
- 주문 상태가 `Processing`으로 멈춘 데이터 식별 후, 수동으로 상태 변경.
- 누락된 주문 데이터는 사용자 로그를 기반으로 재처리.

---

### 재발 방지 대책

#### 1. 숏텀 (ETA: 12.20, 담당: @철진)
- **JVM 힙 메모리 최적화**:
    - JVM 설정을 `-Xms512g -Xmx1024g`로 조정하여 메모리 여유 확보.
- **트래픽 제한 적용**:
    - Nginx나 API Gateway에서 요청 제한 설정.

#### 2. 미드텀 (ETA: 1.31, 담당: @철진)
- **오토스케일링 설정**:
    - CPU/메모리 부하에 따라 서버 자동 증설.
    - 주문 서버 복제본을 최소 2대에서 최대 10대까지 스케일링 가능하도록 설정.
- **비동기 처리 적용**:
    - 주문 데이터 저장 및 상태 변경 로직을 Kafka 큐를 활용한 비동기 처리로 전환.
- **부하 테스트 강화**:
    - K6를 활용하여 부하 테스트를 정기적으로 수행.

#### 3. 롱텀 (ETA: 3.31, 담당: @철진)
- **서버리스 아키텍처 도입**:
    - 주문 API를 Lambda 함수와 같은 서버리스 아키텍처로 이전하여 무제한 스케일링 가능하도록 설계.
- **실시간 모니터링 및 알림 강화**:
    - Prometheus 및 Grafana를 활용하여 JVM 메모리, 트래픽, 스레드 상태 등의 주요 지표를 실시간으로 모니터링.
    - 장애 발생 시 즉각 알림(예: Slack, 이메일).

---
