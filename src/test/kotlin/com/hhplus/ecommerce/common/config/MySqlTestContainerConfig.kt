package com.hhplus.ecommerce.common.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class MySqlTestContainerConfig {
    companion object {
        private val mySQLContainer = MySQLContainer(DockerImageName.parse("mysql:8.0"))
            .apply {
                withUsername("testuser")
                withPassword("testpassword")
                withDatabaseName("testdb")
                withReuse(true)
            }

        init {
            mySQLContainer.start()

            // Spring Boot 애플리케이션에서 사용할 데이터베이스 정보 동적 설정
            System.setProperty(
                "spring.datasource.url",
                "jdbc:mysql://localhost:${mySQLContainer.getMappedPort(3306)}/testdb"
            )
            System.setProperty("spring.datasource.username", mySQLContainer.username)
            System.setProperty("spring.datasource.password", mySQLContainer.password)
        }

    }

    @Bean
    fun mysqlContainer(): MySQLContainer<*> {
        return mySQLContainer
    }
}