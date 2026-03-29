package com.hhplus.ecommerce.common.advice

import com.hhplus.ecommerce.balance.infrastructure.exception.BalanceLimitExceededException
import com.hhplus.ecommerce.balance.infrastructure.exception.InsufficientBalanceException
import com.hhplus.ecommerce.cart.infrastructure.exception.DuplicatedProductException
import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.common.exception.ControllerException
import com.hhplus.ecommerce.common.exception.FacadeException
import com.hhplus.ecommerce.common.exception.NotFoundException
import com.hhplus.ecommerce.common.exception.RepositoryException
import com.hhplus.ecommerce.common.exception.ServiceException
import com.hhplus.ecommerce.order.infrastructure.exception.InvalidOrderStatusException
import com.hhplus.ecommerce.product.infrastructure.exception.OutOfStockException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {

    // Controller 계층 예외 처리 → 400
    @ExceptionHandler(ControllerException::class)
    fun handleControllerException(e: ControllerException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Controller error")
        logger.error("ControllerException: {}", e)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    // Service 계층 예외 처리 → 422
    @ExceptionHandler(ServiceException::class)
    fun handleServiceException(e: ServiceException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Service error")
        logger.error("ServiceException: {}", e)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response)
    }

    // Facade 계층 예외 처리 → 409 (비즈니스 규칙 충돌)
    @ExceptionHandler(FacadeException::class)
    fun handleFacadeException(e: FacadeException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Facade error")
        logger.error("FacadeException: {}", e)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    // 비즈니스 규칙 충돌 예외 처리 → 409
    @ExceptionHandler(
        InsufficientBalanceException::class,
        OutOfStockException::class,
        BalanceLimitExceededException::class,
        DuplicatedProductException::class,
        InvalidOrderStatusException::class,
    )
    fun handleConflictException(e: RuntimeException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Conflict error")
        logger.error("ConflictException: {}", e)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    // 리소스 Not Found 예외 처리 → 404
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Not found")
        logger.error("NotFoundException: {}", e)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    // Repository 계층 예외 처리 → 500 (위 핸들러에서 처리되지 않은 나머지)
    @ExceptionHandler(RepositoryException::class)
    fun handleRepositoryException(e: RepositoryException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Repository error")
        logger.error("RepositoryException: {}", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    // Bean Validation 예외 처리 → 400
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<CustomErrorResponse> {
        val message = e.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        val response = CustomErrorResponse.fail(message)
        logger.error("ValidationException: $message")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    // 파라미터 타입 불일치 예외 처리 → 400
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(e: MethodArgumentTypeMismatchException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail("잘못된 파라미터 타입입니다: ${e.name}")
        logger.error("MethodArgumentTypeMismatchException: ${e.message}")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    // 잘못된 JSON 형식 예외 처리 → 400
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val response = CustomErrorResponse.fail("잘못된 요청 형식입니다.")
        logger.error("HttpMessageNotReadableException: ${ex.message}")
        @Suppress("UNCHECKED_CAST")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) as ResponseEntity<Any>
    }

    // 존재하지 않는 엔드포인트 요청 → 404
    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val response = CustomErrorResponse.fail("요청한 리소스를 찾을 수 없습니다: ${ex.requestURL}")
        logger.error("NoHandlerFoundException: ${ex.message}")
        @Suppress("UNCHECKED_CAST")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response) as ResponseEntity<Any>
    }

    // 그 이외의 계층 Exception → 500
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message.toString())
        logger.error("Exception: {}", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}
