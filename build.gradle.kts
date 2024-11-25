plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("kapt") version "1.9.0" // Kotlin 버전에 맞는 kapt 플러그인
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
	annotation("jakarta.persistence.MappedSuperclass")
}

//
//kapt {
//	arguments {
//		arg("querydsl.entityAccessors", "true")
//		arg("querydsl.addGeneratedAnnotation", "false")
//	}
//}

sourceSets.getByName("main") {
	java.srcDir("build/generated/source/kapt/main")
}

group = "com.hhplus"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// coroutine
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

	// logging
	implementation("org.springframework.boot:spring-boot-starter-logging")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5") // JSON 파싱

	// repository
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("mysql:mysql-connector-java:8.0.33") // MySQL 드라이버 (버전은 최신으로 선택)
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	// runtimeOnly("com.h2database:h2")

	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.redisson:redisson-spring-boot-starter:3.20.0")


	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")
	implementation("jakarta.persistence:jakarta.persistence-api")
	implementation("jakarta.annotation:jakarta.annotation-api")

	// kafka
	implementation("org.springframework.kafka:spring-kafka")

	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	// QueryDSL APT (Annotation Processing Tool)
	kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
	kapt("org.springframework.boot:spring-boot-configuration-processor")

	// test
	// testImplementation("org.testcontainers:testcontainers:1.19.0")
	testImplementation("org.testcontainers:kafka:1.19.0")
	testImplementation("org.testcontainers:junit-jupiter:1.19.0")
	testImplementation("org.testcontainers:mysql:1.19.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
