## 부하 테스트 성능 지표 분석

---

### 서비스 내에서 병목이 발생하는 부분
> 주문 API
- 성능 테스트 결과 보고
  - 초당 100개의 주문 요청을 1분 동안 진행
  - ![order_perf](/docs/mornitoring/order_perf.png)
- 병목 원인
  - Redis Lock으로 인해 재고를 가져오는 부분에서 병목 발생 => max 지표가 2초
- 개선
  - 추후 개선 사항으로, 재고에 대한 Kafka 이벤트 발행/구독을 통해 개선 예정

### 장애 분석 도구
- 프로메테우스 & 그라파나 대시보드
  - 위 주문 API 요청 테스트 간에 CPU, load 등의 지표 상승 발생
  ![grafana.png](/docs/mornitoring/grafana.png)