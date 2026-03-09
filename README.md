# my-tree-back

`my-tree` 프로젝트의 Spring Boot REST API 서버입니다.  
회원가입, 로그인, 회원 조회/삭제, 게시글 등록/조회/삭제 기능을 제공합니다.

## 기술 스택

- Java 21
- Spring Boot 4.0.3
- MyBatis 4.0.0
- Oracle Database
- H2 (테스트 전용)
- Gradle

## 현재 구현 범위

- 회원가입
- 로그인
- 회원 단건 조회
- 회원 목록 조회
- 회원 정보 수정
- 관리자 전용 회원 삭제
- 게시글 등록
- 게시글 전체 조회
- 게시글 단건 조회
- 작성자 본인 또는 관리자 게시글 삭제
- 프론트 개발 서버(`http://localhost:5173`, `http://127.0.0.1:5173`) CORS 허용

## 관리자 계정

애플리케이션 시작 시 아래 관리자 계정을 자동으로 보장합니다.

- 아이디: `admin`
- 비밀번호: `963963`

이미 `admin` 계정이 있어도 시작 시 비밀번호와 이름이 관리자 기준으로 다시 맞춰집니다.

## 실행 환경

기본 설정 파일: [src/main/resources/application.yml](/c:/dev/my-tree/my-tree-back/src/main/resources/application.yml)

```yml
spring:
  datasource:
    driver-class-name: oracle.jdbc.OracleDriver
    url: jdbc:oracle:thin:@localhost:1521:xe
    username: lghr
    password: 12345

server:
  port: 8081
```

기본 포트는 `8081`입니다.

## 사전 준비

1. Java 21 설치
2. Oracle DB 실행
3. `application.yml`의 DB 접속 정보 확인
4. 아래 DDL 실행

Oracle용 스키마 파일: [src/main/resources/db/schema-oracle.sql](/c:/dev/my-tree/my-tree-back/src/main/resources/db/schema-oracle.sql)

```sql
CREATE TABLE users (
  user_id VARCHAR2(50) PRIMARY KEY,
  password VARCHAR2(255) NOT NULL,
  name VARCHAR2(50) NOT NULL,
  created_at DATE DEFAULT SYSDATE NOT NULL,
  ip_address VARCHAR2(50) NOT NULL
);

INSERT INTO users (
  user_id,
  password,
  name,
  created_at,
  ip_address
) VALUES (
  'admin',
  '963963',
  '관리자',
  SYSDATE,
  '127.0.0.1'
);

CREATE SEQUENCE SEQ_POST_NO
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

CREATE TABLE posts (
  post_no NUMBER PRIMARY KEY,
  user_id VARCHAR2(50) NOT NULL,
  title VARCHAR2(150) NOT NULL,
  content VARCHAR2(4000) NOT NULL,
  created_at DATE DEFAULT SYSDATE NOT NULL
);
```

## 실행 방법

```bash
./gradlew bootRun
```

Windows:

```bash
gradlew.bat bootRun
```

## 테스트

테스트는 H2 메모리 DB를 사용합니다.

```bash
./gradlew test
```

Windows:

```bash
gradlew.bat test
```

테스트 설정 파일:

- [src/test/resources/application.yml](/c:/dev/my-tree/my-tree-back/src/test/resources/application.yml)
- [src/test/resources/schema.sql](/c:/dev/my-tree/my-tree-back/src/test/resources/schema.sql)

## API 요약

기본 URL: `http://localhost:8081`

### 1. 회원가입

`POST /api/users/signup`

요청:

```json
{
  "userId": "user01",
  "password": "1234",
  "name": "홍길동"
}
```

제약:

- `userId`: 필수, 최대 50자
- `password`: 필수, 최대 255자
- `name`: 필수, 최대 50자

성공 응답: `201 Created`

```json
{
  "userId": "user01",
  "name": "홍길동",
  "createdAt": "2026-03-09T10:00:00",
  "ipAddress": "127.0.0.1"
}
```

### 2. 로그인

`POST /api/users/login`

요청:

```json
{
  "userId": "user01",
  "password": "1234"
}
```

성공 응답: `200 OK`

### 3. 회원 단건 조회

`GET /api/users/{userId}`

### 4. 회원 목록 조회

`GET /api/users`

관리자 화면에서 회원 목록 탭에 사용됩니다.

### 5. 회원 정보 수정

`PUT /api/users/{userId}`

요청:

```json
{
  "password": "new-password",
  "name": "새 이름"
}
```

### 6. 회원 삭제

`DELETE /api/users/{userId}`

삭제 요청자는 `admin`이어야 합니다.  
요청자 아이디는 아래 3가지 방식 중 하나로 전달할 수 있습니다.

- Query String: `?requesterUserId=admin`
- Header: `X-Requester-User-Id: admin`
- Body:

```json
{
  "requesterUserId": "admin"
}
```

`admin` 계정 자체는 삭제할 수 없습니다.

### 7. 게시글 등록

`POST /api/posts`

요청:

```json
{
  "userId": "user01",
  "title": "첫 메모",
  "content": "안녕하세요."
}
```

제약:

- `userId`: 필수, 최대 50자
- `title`: 필수, 최대 150자
- `content`: 필수, 최대 4000자

성공 응답: `201 Created`

### 8. 게시글 전체 조회

`GET /api/posts`

프론트에서 보드, 테이블 탭 모두 이 API를 사용합니다.

### 9. 게시글 단건 조회

`GET /api/posts/{postNo}`

### 10. 게시글 삭제

`DELETE /api/posts/{postNo}`

삭제 권한:

- 게시글 작성자 본인
- `admin`

요청자 아이디 전달 방식은 회원 삭제와 동일합니다.

## 오류 응답 형식

예외 발생 시 아래 형식으로 응답합니다.

```json
{
  "timestamp": "2026-03-09T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "..."
}
```

주요 상태 코드:

- `400 Bad Request`: 입력값 오류, 관리자 계정 삭제 시도
- `401 Unauthorized`: 로그인 실패
- `403 Forbidden`: 권한 없는 삭제 요청
- `404 Not Found`: 사용자 또는 게시글 없음
- `409 Conflict`: 중복 사용자 아이디

## 프로젝트 구조

```text
src/main/java/com/example/mytree
├─ config
├─ controller
├─ domain
├─ dto
├─ exception
├─ repository
└─ service

src/main/resources
├─ application.yml
├─ db/schema-oracle.sql
└─ mapper
   ├─ PostMapper.xml
   └─ UserMapper.xml
```

## 참고 사항

- 현재 비밀번호는 암호화 없이 저장됩니다.
- 프론트 기준 CORS는 로컬 Vite 개발 서버만 허용되어 있습니다.
- 게시글 위치, 보드 페이지, 색상 등 UI 배치 정보는 프론트 로컬 저장소에서 관리합니다.
