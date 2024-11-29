# 2단계: 실행 단계
FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일 복사
COPY ./build/libs/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# JVM 옵션 (힙 메모리 제한)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 기본 프로파일 설정
ENV SPRING_PROFILES_ACTIVE=dev

# 컨테이너 실행 시 명령어
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app.jar"]
