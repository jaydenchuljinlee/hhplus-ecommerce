---
name: domain-service
description: 새로운 도메인 서비스, Repository, Facade, DTO를 프로젝트 아키텍처에 맞게 생성
---

# 도메인 서비스 생성 컨벤션

새로운 도메인 기능을 추가할 때 이 규칙을 따른다.

## 패키지 구조

```
{domain}/
├── domain/
│   ├── {Domain}Service.kt              # @Service, 비즈니스 로직
│   ├── repository/
│   │   └── I{Domain}Repository.kt      # 인터페이스 (인프라 의존 없음)
│   ├── dto/
│   │   ├── {Action}Command.kt          # 상태 변경 입력 (CQRS)
│   │   ├── {Action}Query.kt            # 조회 입력 (CQRS)
│   │   └── {Domain}Result.kt           # 출력
│   ├── event/
│   │   └── {Domain}ChangedEvent.kt     # 도메인 이벤트
│   └── exception/
│       └── {Domain}ServiceException.kt # 도메인 예외
├── usecase/
│   ├── {Domain}Facade.kt               # @Component, 유스케이스 오케스트레이션
│   └── dto/
│       ├── {Action}Creation.kt         # Facade 입력 (API→Domain 변환)
│       └── {Domain}Info.kt             # Facade 출력 (Domain→API 변환)
└── infrastructure/
    ├── jpa/
    │   ├── entity/{Domain}Entity.kt    # JPA 엔티티
    │   └── {Domain}JpaRepository.kt    # Spring Data JPA
    └── {Domain}Repository.kt           # I{Domain}Repository 구현체
```

## Service 패턴

```kotlin
@Service
class {Domain}Service(
    private val {domain}Repository: I{Domain}Repository,  // 인터페이스 의존
) {
    // 조회: @Transactional 불필요
    fun get{Domain}(query: {Domain}Query): {Domain}Result {
        val entity = {domain}Repository.findById(query.id)
        return {Domain}Result.from(entity)
    }

    // 상태 변경: @Transactional 필수
    @Transactional
    fun create(command: {Action}Command): {Domain}Result {
        val entity = command.toEntity()
        {domain}Repository.insertOrUpdate(entity)
        return {Domain}Result.from(entity)
    }
}
```

## 핵심 규칙

### 의존성 방향
- **Domain Service → I{Domain}Repository (인터페이스)**
- Domain 계층은 infrastructure에 절대 의존하지 않음
- ApplicationEventPublisher는 Spring 프레임워크이므로 허용

### Repository 인터페이스
- 접두사 `I` 사용: `I{Domain}Repository`
- 메서드명: `findById`, `findByXxx`, `insertOrUpdate`, `deleteXxx`
- 예외 처리는 구현체에서 `orElseThrow` 사용

### Facade 패턴
- `@Component` 사용 (@Service 아님)
- 여러 Service를 조율하는 유스케이스 오케스트레이션
- `@Transactional`로 전체 유스케이스를 원자적으로 묶음

### DTO 변환 규칙
- **Request** → `toXxxCommand()` 또는 `toXxxQuery()` → **Domain DTO**
- **Domain Result** → `{Info/Response}.from(result)` → **API Response**
- `companion object { fun from() }` 패턴 사용

### Entity 패턴
- `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`
- `@CreatedDate`, `@LastModifiedDate` 감사 컬럼
- `@Enumerated(EnumType.STRING)` Enum 저장
- 비즈니스 로직 메서드는 Entity에 포함 가능 (DDD Rich Model)

### 동시성 제어
- 분산 락: `@RedisLock(key = "'prefix:' + #param.field")`
- DB 락: `@Lock(LockModeType.PESSIMISTIC_WRITE)` (JPA Repository)

### 예외 계층
- `ControllerException` → HTTP 400
- `ServiceException` → HTTP 422
- `FacadeException` → HTTP 422
- `RepositoryException` → HTTP 500
