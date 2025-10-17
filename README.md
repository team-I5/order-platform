# 주문 관리 플랫폼

## 프로젝트 소개
사용자 · 가게 · 상품 · 주문 · 결제 등 핵심 도메인을 중심으로 설계한 음식점 주문 관리 플랫폼  
<br>
4계층 아키텍처(Controller–Service–Domain–Infrastructure)적용, DDD(Domain-Driven Design) 관점에서 도메인 로직을 명확히 분리하여 응집도 강화   
OCP(개방-폐쇄 원칙) 와 DIP(의존 역전 원칙) 을 적용하여 인프라 계층의 기술 의존성을 최소화하고, 확장성과 유지보수성을 강화

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
🧾[API 명세 - Notion](https://www.notion.so/teamsparta/27a2dc3ef51481c8b6d7c259cbf25112?v=27a2dc3ef51481c29c04000c20372852&source=copy_link)

 
## 인프라 설계도

<img width="806" height="441" alt="Image" src="https://github.com/user-attachments/assets/bcb43344-c723-4645-b851-57abd5574bb5" />

## Conventions
### 네이밍 규칙
|구분|규칙|예시|
|---|---|---|
|클래스|PascalCase|UserService, OrderController|
|변수 / 메서드|camelCase|StoreName, calculateTotal()|
|패키지|소문자, 기능별 구분|com.spartaclub.orderplatform.domain|
|DB 테이블|snake_case + p_접두사|p_user, p_category|
|DB 컬럼|snake_case|created_at, store_address|

### 코드 스타일
📝[java-google 코드 스타일](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)

### Package Structure
```
main.java.com.spartaclub.orderplatform
├─ domain
│    ├─ ai
│    │   ├─ application
│    │   ├─ domain
│    │   ├─ infrastructure
│    │   └─ presentation
│    │
│    ├─ category
│    │   ├─ application
│    │   ├─ domain
│    │   ├─ exception
│    │   ├─ infrastructure
│    │   └─ presentation
│    │
│    ├─ order
│    │   └─ ...
│    │
│    ├─ payment
│    │   └─ ...
│    │
│    ├─ product
│    │   └─ ...
│    │
│    ├─ review
│    │   └─ ...
│    │
│    ├─ store
│    │   └─ ...
│    │
│    └─ user
│        └─ ...
│
└─ global
     ├─ application.security
     │
     ├─ auth
     │   ├─ exception
     │   ├─ handler
     │   ├─ jwt
     │   └─ sevice
     │
     ├─ config
     │   ├─ auditing
     │   ├─ security
     │   └─ web
     │
     ├─ domain.entity
     │
     ├─ exception
     │   └─ advice
     │
     ├─ infrastructure.config.swagger
     │
     ├─ presentation.dto
     │
     └─ util
```

## 핵심 기능

## 인증 · 인가 흐름에 대한 도식화
![](https://cdn.discordapp.com/attachments/1413215340514640041/1428647586193084437/2025-10-17_4.34.32.png?ex=68f3433f&is=68f1f1bf&hm=04920b177a0fdd3acd50cfeb140ff5c4151b1c1f63ab3f20b345afdb52c64f06)


## 트러블 슈팅
### Mapstruct 맵핑 중 발생한 Bean 문제 해결
#### 문제 상황
Entity 생성할 때 Mapstruct를 사용해서 requestDto에서 받아온 값들을 service에서 toEntity 메서드 통해 entity로 전달 하는데 Bean관련 오류 발생
#### 원인 
Entity 객체로 정보 전달할 때 온전하게 외래 키 관계 Entity 객체 생성 못해서 맵핑 중 문제 발생한 것으로 추측
#### 해결 방법 
외래 키와 findById 메서드 활용해 외래 키 엔티티 받아와 생성할 정적 팩터리 메서드에 파라미터로 넣어 전달한 후,      
Entity 클래스에서 온전한 하나의 행으로 DB에 반영되도록 엔티티 객체 생성하도록 하여 해결

### AI 요청 로그 저장 방식 최적화
#### 문제 상황 
AI 상품 설명 요청 시마다 로그를 DB에 즉시 저장하도록 설계    
요청을 할 때 마다 이전 로그의 상태를 USED -> NO_USE로 수정    
결과적으로, 단일 상품 생성 시 여러 번의 update 쿼리가 발생
#### 원인 
모든 요청을 즉시 DB에 반영하는 구조   
상품 저장 시점까지 “임시 상태”임에도 지속적으로 DB에 접근
#### 해결 방법
요청 로그를 캐시에 임시 저장하도록 리팩토링
 1. 모든 로그를 캐시에 NO_USE 상태로 저장
 2. 상품 저장 후 캐시에 저장된 해당 상품의 로그 중 마지막 요청을 비교
 3. 상품에 저장된 설명과 로그의 응답 설명이 같으면 USED로 변경
 4. DB에 로그 저장 후 캐시 비움

### EC2 Docker 배포 시 OpenJDK 이미지 문제 해결
#### 문제 상황 
EC2에서 openjdk:17-jre-slim 이미지 빌드 실패     
로컬에서는 정상 동작, EC2에서만 "not found" 오류 발생 
#### 원인 
Docker Hub에서 openjdk:17-jre-slim 이미지 deprecated     
Alpine 기반 이미지에서 사용자 생성 명령어 차이로 추가 오류 발생 
#### 해결 방법
Base Image 변경: amazoncorretto:17-alpine      
Alpine 환경에 맞게 사용자 생성 명령어 수정 : addgroup -S springboot / adduser -S springboot -G springboot

### N+1 문제 예방 및 리뷰 집계 스케줄링
#### 문제상황
음식점 목록 조회 시 각 음식점에 연결된 리뷰엔티티를 통해 리뷰 평점과 리뷰 수를 실시간으로 계산하려고 하면, 반복적으로 DB 조회하게 되어 N+1문제 발생할 수 있음
#### 원인 
반복문 안에서 각 음식점의 연관 리뷰를 접근하면 쿼리 반복 실행, 매 요청 시마다 집계 쿼리를 실행하면 트래픽이 많은 시간대에 DB 부하 발생할 수 있음
#### 해결 방법
LEFT JOIN과 GROUP BY를 활용해 모든 리뷰 한번에 집계해 반복문 안에서는 필드만 수정하고 Lazy 연관 접근하지 않아 N+1 문제 방지 

### 원플로우 이벤트 기반 처리 → 단계별 API 분리
#### 문제상황
주문 생성 후 트랜잭션 커밋 시 @TransactionalEventListener(phase = AFTER_COMMIT)로 결제 도메인 이벤트 발행 → 결제 생성 및 승인 처리
#### 문제점
이벤트 유실·중복 위험, 재시도 시 정확한 상태 보장 어려움    
주문-결제 도메인 간 강한 결합 → 요청/응답 흐름 복잡, 응답 지연 증가
#### 개선 방향
API를 명확히 분리하고 상태 전이를 각 도메인에 응집 → 도메인 간 의존성 약화 결합도↓ 응집도↑, 응답 속도 단축, 장애 격리성 개선


## 공통 관심사항
### 공통 엔티티 관리(BaseEntity)
- createdAt/modifiedAt, createdId/modifiedId: 생성 및 수정 시각, ID(자동 관리, JPA Auditing 사용)
- deletedAt, deletedId: 소프트 삭제 시각, ID
- 작성/수정 정보 관리 자동화, 코드 중복 제거 및 데이터 무결성 확보
  
### 엔티티 작성/수정 관리(AuditorAwareImpl)
- @EnableJpaAuditing(auditorAwareRef = “빈의 이름”)
  - AuditorAware 빈 참조 이름을 지정해 Auditing을 활성화
  - Entity에는 @EntityListeners(AuditingEntityListener.class) 또는 전역 등록을 동작
- AuditingEntityListener
  - JPA Entity 라이프사이클 이벤트를 리스닝해서 auditing 동작을 실행한다.
  - Entity가 persist/update 될 때 자동으로 호출해 어노테이션 처리
- AuditorAware<t>
  - 현재 감사자를 반환하는 인터페이스. getCurrentAuditor()를 통해 Optional로 받아온다.

### 웹 요청(WebConfig)
- WebMvcConfigurer의 구현체인 WebConfig의 addArgumentResolvers 메서드에서 HandlerMethodArgumentResolver 목록에 사용자 정의 resolver를 추가

### 페이지네이션 정책(PageableHandler)
- HandlerMethodArgumentResolver를 구현해 Pageable 타입 파라미터를 바인딩
- supportsParameter: 파라미터 타입이 Pageable이면 true
- resolveArgument: page, size, sort 파라미터를 읽어 PageRequest.of(page,size,sort) 반환
- 정책
  - 허용된 sizes: 10, 30, 50 (그 외 기본 10)
  - 기본 page: 0
  - 허용 sort fields: createdAt, rating, totoalPrice, paymentAmount, averageRating, reviewCount
  - 기본 sort: createdAt, DESC

### API 응답 통일(ApiResponse)
- 모든 API 응답을 통일된 구조로 반환
- 에러 메세지 및 데이터 구조 일관성 유지

### 전역 예외 처리(GlobalExceptionHandler)
- 모든 컨트롤러에서 발생하는 예외 전역 처리
- 처리 범위:
  - MethodArgumentNotValidException: 유효성 검증 실패
  - BusinessException: 비즈니스 로직 예외
  - Exeption: 기타 모든 예외


## 회고
### 잘한 점
- 공통 예외 처리 정책을 구성하고 적용한 점
- SOLID 원칙을 고려하며 코드를 구성한 점
- Builder와 Setter를 제거하고 정적 팩토리 메서드로 엔티티 생성 책임을 명확히 분리하여 불변성을 강화한 점

### 어려웠던 점
- 글로만 보았던 디자인 패턴을 직접 코드에 적용하는 과정에서의 어려움
- 각자 작업한 코드를 통합하면서 충돌과 오류가 발생해 이를 해결하는 과정의 어려움
- DTO를 Entity로 변환할 때 외래 키 관계와 의존성 관리 문제의 어려움

### 한계점과 발전 계획
- 도메인간 결합도를 느슨하게 하기 위해 직접 참조 방식에서 간접 참조로 변경
- 공통 로깅 정책을 적용해 모든 API 요청/응답과 예외상황을 일관되게 기록
- 서비스 안정성을 높이기 위해 헬스체크와 모니터링 체계를 적용

### 협업에서 아쉬운 부분
- 팀원 간 작업 속도 차이로 인해 공통 모듈이나 기능 개발이 지연됨 → 느린 진행을 조기에 파악하고, 페어 프로그래밍 지원   
- 느린 진행 때문에 다른 기능 개발이나 테스트 일정에도 영향을 줌  → 작업 단위를 세분화하고 우선순위에 따라 계획 조정 


## 팀원 소개

| 김한결 | 류형선 | 이준성 | 이현주 | 전우선 |
|---|---|---|---|---|
| <img src="https://avatars.githubusercontent.com/u/50177492?v=4" width="250"> | <img src="https://avatars.githubusercontent.com/u/115407275?v=4" width="250"> </div> | <img src="https://avatars.githubusercontent.com/u/58545479?v=4" width="250"> </div> | <img src="https://avatars.githubusercontent.com/u/124776145?v=4" width="250"> </div> | <img src="https://avatars.githubusercontent.com/u/150002756?v=4" width="250"> </div> |
| <div align="center"><a href="https://github.com/kmr5326" target="_blank">@kmr5326</a></div> | <div align="center"><a href="https://github.com/ooinl77" target="_blank">@ooinl77</a></div> | <div align="center"><a href="https://github.com/junes7" target="_blank">@junes7</a></div> | <div align="center"><a href="https://github.com/guswnee00" target="_blank">@guswnee00</a></div> | <div align="center"><a href="https://github.com/wooxexn" target="_blank">@wooxexn</a></div> |
|<div align="center">Order, Payment</div>|<div align="center">Product, Option, AI</div>|<div align="center">Review, Category</div>|<div align="center">Store</div>|<div align="center">User, Address, Auth</div>|
|<div align="center">테크리더</div>|<div align="center">팀원</div>|<div align="center">팀원</div>|<div align="center">팀장</div>|<div align="center">팀원</div>|

