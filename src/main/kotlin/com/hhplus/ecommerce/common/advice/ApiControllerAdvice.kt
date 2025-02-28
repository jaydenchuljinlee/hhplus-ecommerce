package com.hhplus.ecommerce.common.advice

import com.hhplus.ecommerce.common.dto.CustomErrorResponse
import com.hhplus.ecommerce.common.exception.ControllerException
import com.hhplus.ecommerce.common.exception.FacadeException
import com.hhplus.ecommerce.common.exception.RepositoryException
import com.hhplus.ecommerce.common.exception.ServiceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {

    // Controller 계층 예외 처리
    @ExceptionHandler(ControllerException::class)
    fun handleControllerException(e: ServiceException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Controller error")
        logger.error("ControllerException: $e")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    // Service 계층 예외 처리
    @ExceptionHandler(ServiceException::class)
    fun handleServiceException(e: ServiceException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Service error")
        logger.error("ServiceException: {}", e)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response)
    }

    // Repository 계층 예외 처리
    @ExceptionHandler(RepositoryException::class)
    fun handleRepositoryException(e: RepositoryException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Repository error")
        logger.error("RepositoryException: {}", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    // Facade 계층 예외 처리
    @ExceptionHandler(FacadeException::class)
    fun handleFacadeException(e: FacadeException): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message ?: "Facade error")
        logger.error("FacadeException: {}", e)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response)
    }

    // 그 이외의 계층 Exception
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<CustomErrorResponse> {
        val response = CustomErrorResponse.fail(e.message.toString())
        logger.error("Exception: {}", e)
        return ResponseEntity.status(response.status).body(response)
    }
}