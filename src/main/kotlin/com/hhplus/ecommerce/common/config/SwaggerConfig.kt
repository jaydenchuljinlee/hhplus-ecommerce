package com.hhplus.ecommerce.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI().components(Components()).info(apiInfo())

    private fun apiInfo(): Info = Info().title("E-Commerce API 문서").description("Swagger UI").version("1.0.0")
}