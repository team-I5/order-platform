# 주문 관리 플랫폼

## 프로젝트 소개

## 개발 환경

| 분류               | 상세                                                                            |
|------------------|-------------------------------------------------------------------------------|
| IDE              | IntelliJ                                                                      |
| Language         | Java 17                                                                       |
| Framework        | Spring Boot 3.5.5, Spring Data JPA, Spring Security + JWT, Validation, Lombok |
| Database         | PostgreSQL 14                                                                 |
| Build Tool       | Gradle                                                                        |
| Development Tool | Swagger, Postman, Mockito                                                     |
| Collaboration    | Discord, Notion, Zep                                                          |

## ERD

<img width="2087" height="938" alt="Image" src="https://github.com/user-attachments/assets/8dd254cf-5248-488a-9ed3-5ab3c52b95cc" />

## 도메인 다이어그램
![Image](https://github.com/user-attachments/assets/238e175a-8700-420d-8c38-dbe83e1d0b5e)

## 프로젝트 실행 가이드

### 환경 설정

1. 데이터베이스 설정

```sql
# Postgresql 데이터베이스 생성
CREATE
DATABASE orderplatform;
```

2. 환경변수 설정 (.env.example)

```bash
# 데이터베이스 설정
POSTGRES_DB=your_database_name
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_PORT=5432

# 애플리케이션 설정
APP_PORT=8080
SPRING_PROFILES_ACTIVE=docker
SPRING_APPLICATION_NAME=order-platform

# JWT 설정 (강력한 키로 변경하세요)
JWT_SECRET=your_jwt_secret_key_here_at_least_32_characters_long
JWT_ACCESS_TOKEN_EXPIRATION=900000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# Google Gemini AI 설정 (본인의 API 키로 변경하세요)
GOOGLE_GEMINI_API_KEY=your_google_gemini_api_key_here
GOOGLE_GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent

# 로깅 레벨
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_APP=DEBUG
LOGGING_LEVEL_SECURITY=DEBUG
LOGGING_LEVEL_SQL=DEBUG
LOGGING_LEVEL_SQL_BINDER=TRACE

# 컨테이너 이름
POSTGRES_CONTAINER_NAME=order-platform-db
APP_CONTAINER_NAME=order-platform-app

# 네트워크
NETWORK_NAME=order-platform-network
```

3. 애플리케이션 실행

```bash
# docker
# 이미지 빌드 후 컨테이너 실행 (백그라운드 모드)
docker compose up -d --build
# 컨테이너 상태 확인
docker compose ps
#예시)
#  NAME                     COMMAND                  STATE   PORTS
#orderplatform-postgres   "docker-entrypoint.s…"   Up      0.0.0.0:5432->5432/tcp
#orderplatform-app        "java -jar app.jar"      Up      0.0.0.0:8080->8080/tcp

# gradle
./gradlew bootRun


```

4. docker-compose

📄 [docker-compose.yml ](https://github.com/team-I5/order-platform/blob/develop/docker-compose.yml)

5. 초기 데이터 유저 목록

```
유저 목록 / 비밀번호: Asd123456!
-------------------
Customer
hong@test.com
oh@test.com
-------------------
Owner
lee@test.com
lucas@test.com
-------------------
Master
mk@test.com
```

## API

#### API 총 66개 (설계 대비 구현률 : 100 %)
| API                   | 개수                   | API                   | 개수                   |
|-----------------------|------------------------|-----------------------|------------------------|
| 사용자 (User)         | 8개                     | 인증 및 토큰(Auth)     | 1개                    |
| 주소(Address)         | 4개                     | 음식점(Store)         | 13개                    |
| 상품 (Product)        | 9개                     | 상품 설명(AI)          | 1개                    |
| 상품 옵션(Option)      | 6개                    | 주문(Order)            | 7개                    |
| 결제(Payment)          | 5개                    | 리뷰(Review)           | 7개                    |
| 카테고리(Category)     | 5개                    |                        |                        |

#### 상세 내용
🧾[api 명세 - Notion](https://www.notion.so/teamsparta/27a2dc3ef51481c8b6d7c259cbf25112?v=27a2dc3ef51481c29c04000c20372852&source=copy_link)

 
## 인프라 설계도

<img width="806" height="441" alt="Image" src="https://github.com/user-attachments/assets/bcb43344-c723-4645-b851-57abd5574bb5" />

## Conventions

## 핵심 기능

## 트러블 슈팅

## 공통 관심사항

## 회고

