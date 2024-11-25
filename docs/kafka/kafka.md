## 카프카 연동 보고서

> 연동 방법
```yaml
  kafka:
    image: confluentinc/cp-kafka:latest
    container_name: hhplus-kafka
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: hhplus-zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT_INTERNAL://hhplus-kafka:9092,PLAINTEXT_EXTERNAL://localhost:9093
      KAFKA_LISTENERS: PLAINTEXT_INTERNAL://0.0.0.0:9092,PLAINTEXT_EXTERNAL://0.0.0.0:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT_INTERNAL:PLAINTEXT,PLAINTEXT_EXTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT_INTERNAL
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    ports:
      - "9092:9092"  # 내부 리스너
      - "9093:9093"  # 외부 리스너
    networks:
      - app-net

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    environment:
      KAFKA_CLUSTERS_0_NAME: hhplus-cluster
      KAFKA_CLUSTERS_0_BOOTSTRAP_SERVERS: hhplus-kafka:9092
    ports:
      - "8081:8080"
    depends_on:
      - kafka
    networks:
      - app-net
```

> 연동 테스트 코드
```kotlin
@DisplayName("success: 카프카 연동 테스트")
@Test
fun testKafka() {
    // 1. 메시지 발행
    val topic = "KAFKA_TOPIC"
    val message = "카프카 메시지"
    kafkaTemplate.send(topic, message)

    // 2. 메시지 수신 검증
    Thread.sleep(3000) // 메시지 처리 대기
    assertEquals(message, kafkaTestConsumer.message)
}
```

> 토픽 생성
![topic](/docs/kafka/kafka_topic.png)

> 컨슈머 확인
![consumer](/docs/kafka/kafka_consumer.png)

> 개선해야할 점
- 현재 테스트는 연동만을 위한 테스트로, main 패키지에 따로 비즈니스 로직을 만들지 않았습니다.
- 따라서, 해당 테스트는 EmbeddedKafka를 사용하는 대신 외부에 Docker가 실행된 상태를 가정하여 진행했습니다.
- 만약 테스트가 실제 테스트 환경만을 위한 비즈니스 로직 테스트라면, 해당 로직은 모킹하여 테스트를 진행해야 할 것 같습니다.