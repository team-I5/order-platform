# Multi-stage build를 사용하여 이미지 크기 최적화
FROM amazoncorretto:17-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 build.gradle 파일들을 먼저 복사 (캐시 최적화)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle Wrapper에 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (캐시 레이어)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew build -x test --no-daemon

# 실행 단계
FROM amazoncorretto:17-alpine

# 애플리케이션 실행을 위한 사용자 생성 (보안)
RUN groupadd -r springboot && useradd -r -g springboot springboot

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 파일 권한 설정
RUN chown springboot:springboot app.jar

# 사용자 전환
USER springboot

# 포트 노출
EXPOSE 8080

# JVM 옵션 설정 및 애플리케이션 실행
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=docker", \
    "-jar", \
    "app.jar"]