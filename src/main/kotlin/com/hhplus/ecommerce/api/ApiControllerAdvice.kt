package com.hhplus.ecommerce.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<CustomErrorResponse> {

        val response = CustomErrorResponse.fail(e.message.toString())

        return ResponseEntity.status(response.status).body(response)
    }
}