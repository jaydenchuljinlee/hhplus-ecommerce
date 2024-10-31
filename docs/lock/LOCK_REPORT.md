## 동시성 Lock 보고서

## 현재 시나리오에서 발생할 수 있는 동시성 이슈

- 재고 차감: 여러 사용자가 동시에 `상품 A`를 주문할 경우, 상품에 대한 `재고 동시성 이슈`가 발생한다.
- 잔액 사용: 주문 결제 시, 사용자가 여러 탭을 띄워놓고 결제를 하는 경우 혹은 여러 상품을 결제했는데 결제 처리가 뒤늦게 한 번에 처리되는 경우 `동시성 이슈`가 발생한다.
- 잔액 충전: 주문 결제 시, 사용자 잔액 차감이 발생할 때 충전을 하게 되면 `잔액 정합성 이슈`가 발생한다.

---

## 현재 채택한 동시성 제어 방식
- **잔액 차감 or 충전**: Redis의 Pub/Sub 락 사용
  - 발생 가능성은 낮지만, 꼭 한 번만 성공하라는 보장은 없기 때문에 동시성 제어 목적으로 사용했다.
- **재고 차감**: Redis의 Pub/Sub 락 사용
  - 동시성 이슈가 빈번하게 발생하는 상황이기 때문에 재고 관리 목적으로 사용했다.

---

## DB 비관적 락에 대한 분석

- 비관적 락의 경우 동시성 이슈가 발생할 확률이 높은 경우 사용한다.
- 현재 시나리오에 대헤 기존에 채택한 동시성 제어 방식이다.
- 비관적 락을 도입하기 위해서는 대표적으로 `PESSIMISTIC_READ` 과 `PESSIMISTIC_WRITE`이 존재
  - PESSIMISTIC_READ: 읽기를 허용하는 수준의 락으로, 시나리오의 동시성 제어를 위해서는 맞지 않는다.
  - PESSIMISTIC_WRITE: 읽기를 허용하지 않는 배타락으로, 시나리오의 동시성 처리 방향성과 일치하여 사용했었다.

> 사용 방식
```kotlin
interface BalanceJpaRepository: JpaRepository<BalanceEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT b FROM BalanceEntity b WHERE b.userId = :userId")
    fun findByUserIdWithLock(@Param("userId") userId: Long): Optional<BalanceEntity>
}
```

> 채택 여부
- **복잡성**: 단순하게 `@Lock` 어노테이션만 사용하면 되기 때문에 쉽게 사용할 수 있다.
- **효율성**: DB락을 걸기 때문에 `DB 부하`가 생기게 되지만, 단일 환경에 단순한 구조라면 나쁘지 않다고 생각한다. 
- 비관적 락의 경우, `분산 환경`에서 DB 제어가 어려워지기 때문에 이번 챕터에서는 채택하지 않았다.

---

## Redis의 스핀락에 대한 분석
- 스핀락은 Redis 분산락의 한 종류로, 다음과 같이 락을 얻을 때까지 while문을 순회하게 된다.
- 이는 곧 CPU Intensive 한 작업이 반복되기 때문에 서버 부하를 야기한다.
- 또한, 해당 Lock은 적정 시간을 두고 폴링하는 방식이기 때문에 시기적절하게 락이 해제되고 바로 가져오지 못 한다.
  - 물론, 타이밍이 맞게 가져올 수는 있지만, 계속 락을 얻기 위해 문을 두드린다는 단점은 변하지 않는다.

> 사용 방식
```kotlin
@Component
class SpinLockSupporter(
    private val redissonClient: RedissonClient
): IRedisLockSupporter {
    private val logger = LoggerFactory.getLogger(SpinLockSupporter::class.java);

    override fun <T> withLock(key: String, waitTime: Long, releaseTime: Long, action: () -> T) {
        val spinLock = redissonClient.getSpinLock(key)

        if (spinLock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.info("REDIS:LOCK:SPINE:$key")
            try {
                action()
            } finally {
                spinLock.unlock()
            }
        }

    }
}
```
> 채택여부
- 복잡도: 현재 구현 방식은 Lettuce를 통해서도 구현할 수 있지만, Redisson에서도 간단하게 구현이 가능하다.
- 효율성: 다음 Redisson의 RedissonSpinLock 클래스 내 스핀락 코드를 보면, 다음과 같이 락을 얻을 때까지 지속적으로 Sleep을 걸었다가 호출하는 것을 알 수 있다.
  - 이는 서버 부하를 줄 수 있기 때문에 효율성 측면에서 좋지 않다고 생각하여 채택하지 않았다.
```java
public void lockInterruptibly(long leaseTime, TimeUnit unit) throws InterruptedException {
        long threadId = Thread.currentThread().getId();
        Long ttl = this.tryAcquire(leaseTime, unit, threadId);
        if (ttl != null) {
            for(LockOptions.BackOffPolicy backOffPolicy = this.backOff.create(); ttl != null; ttl = this.tryAcquire(leaseTime, unit, threadId)) {
                long nextSleepPeriod = backOffPolicy.getNextSleepPeriod();
                Thread.sleep(nextSleepPeriod);
            }

        }
    }
```

---

## PUB/SUB 락과 공정락에 대한 분석
- 둘 다 Pub/Sub 기반의 동시성을 제어할 수 있는데,
  - 이는 다른 스레드가 락을 얻기 위해 지속적으로 요청을 보내지 않아도 현재 락을 점유하고 있는 스레드가 락을 해제하는 시점에 이를 구독하고 있는 다른 스레드에게 알림을 주는 방식이다.   
- Fair Lock의 경우 약간의 오버헤드가 발생하지만, 내부적으로 Queue 를 사용하기 때문에 순서를 보장해주는 특징이 있다.

> 사용 방법
```kotlin
// Fair Lock
@Component
class FairLockSupporter(
    private val redissonClient: RedissonClient
): IRedisLockSupporter {
    private val logger = LoggerFactory.getLogger(FairLockSupporter::class.java);

    override fun <T> withLock(key: String, waitTime: Long, releaseTime: Long, action: () -> T) {
        val fairLock: RLock = redissonClient.getFairLock(key)

        if (fairLock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.info("REDIS:LOCK:FAIR:$key")
            try {
                action()
            } finally {
                fairLock.unlock()
            }
        }
    }
}

// ---

// 일반 Pub/Sub Lock
@Component
class PubSubLockSupporter(
    private val redissonClient: RedissonClient
): IRedisLockSupporter {
    private val logger = LoggerFactory.getLogger(PubSubLockSupporter::class.java);

    override fun <T> withLock(key: String, waitTime: Long, releaseTime: Long, action: () -> T) {
        val lock = redissonClient.getLock(key)

        if (lock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS)) {
            logger.info("REDIS:LOCK:READ_WRITE:$key")
            try {
                action()
            } finally {
                lock.unlock()
            }
        }
    }
}
```

> 채택 여부
- redisson의 기본 Lock과 Fair Lock은 둘다 Pub/Sub 방식을 제공한다.
- 복잡성: 둘 다 메서드 기반으로 캡슐화가 잘 되어 있어서 복잡도는 낮다.
- 효율성: 스핀락과는 달리 락을 해제하는 시점에 다른 스레드가 락을 얻을 수 있어서 이를 채택하게 되었다.
  - 순서가 중요한 상황이라면 Fair Lock을 사용했겠지만, 현재 Queue 를 사용해야할 동시성 이슈는 없다.
  - 따라서 기본적으로 제공해주는 `getLock()` 을 사용하여 `Pub/Sub Lock` 을 통해 동시성 제어를 진행했다. 

--- 

## Kafka에 대한 고려
- 주문 재고 처리 시, 카프카의 이점에 대해서는 익히 알고 있지만 다음과 같은 이유로 이번 챕터에서는 고려하지 않았다.
  - 러닝커브: 레디스의 단순한 사용과는 달리, 카프카는 높은 러닝 커브로 인한 구현의 어려움이 크다고 판단했다.
  - 복잡성: 현재 구조는 이벤트 기반으로 동작하는 구조가 아니다 보니, 단기간에 구조를 바꾸기에는 복잡도가 높다고 판단했다.