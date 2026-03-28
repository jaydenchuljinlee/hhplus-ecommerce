---
name: api-endpoint
description: 새로운 API 엔드포인트(Controller, Request/Response DTO) 추가 시 프로젝트 컨벤션에 맞게 생성
---

# API 엔드포인트 생성 컨벤션

새로운 API 엔드포인트를 추가할 때 이 규칙을 따른다.

## 패키지 구조

```
{domain}/api/
├── I{Domain}Controller.kt      # 인터페이스 (Swagger 애너테이션 포함)
├── {Domain}Controller.kt        # 구현체 (@RestController)
└── dto/
    ├── {Action}Request.kt       # 요청 DTO
    └── {Action}Response.kt      # 응답 DTO
```

## Controller 인터페이스 패턴

```kotlin
@RequestMapping("{domain}")
interface I{Domain}Controller {
    @Tag(name = "{도메인 기능 설명}")
    @Operation(summary = "{API 요약}", description = "{API 상세 설명}")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        ApiResponse(responseCode = "500", description = "서버 오류",
            content = [Content(schema = Schema(implementation = CustomErrorResponse::class))]),
    ])
    @PostMapping()
    fun actionName(@RequestBody request: ActionRequest): CustomApiResponse<ActionResponse>
}
```

## Controller 구현체 패턴

```kotlin
@RestController
class {Domain}Controller(
    private val {domain}Facade: {Domain}Facade
): I{Domain}Controller {
    override fun actionName(request: ActionRequest): CustomApiResponse<ActionResponse> {
        val result = {domain}Facade.action(request.toActionCommand())
        return CustomApiResponse.success(ActionResponse.from(result))
    }
}
```

## Request DTO 패턴

- `@Schema`, `@Parameter` 애너테이션으로 문서화
- `toXxx()` 메서드로 도메인 DTO 변환
- 검증 로직은 `init` 블록에 작성

```kotlin
data class ActionRequest(
    @Schema(description = "사용자 ID", example = "1")
    @Parameter(description = "사용자 ID", required = true)
    var userId: Long,
) {
    fun toActionCommand(): ActionCommand {
        return ActionCommand(userId = userId)
    }
}
```

## Response DTO 패턴

- `companion object`의 `from()` 메서드로 도메인 결과 변환

```kotlin
class ActionResponse(
    @Schema(description = "ID", example = "1")
    var id: Long,
) {
    companion object {
        fun from(info: ActionInfo): ActionResponse {
            return ActionResponse(id = info.id)
        }
    }
}
```

## 응답 래핑

- 성공: `CustomApiResponse.success(data)`
- 모든 응답은 `CustomApiResponse<T>` 래핑 필수

## 예외 처리

- Controller 검증 오류: `ControllerException` 상속 → HTTP 400
- Service 비즈니스 오류: `ServiceException` 상속 → HTTP 422
- Repository 데이터 오류: `RepositoryException` 상속 → HTTP 500
- Facade 오류: `FacadeException` 상속 → HTTP 422
