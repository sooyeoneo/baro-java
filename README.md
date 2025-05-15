# Baro Java Application 🧭  
JWT 기반 인증/인가 기능이 포함된 Spring Boot 백엔드 서버

---

## 📌 배포 주소
- **EC2**: [http://ec2-43-203-68-225.ap-northeast-2.compute.amazonaws.com:8080](http://ec2-43-203-68-225.ap-northeast-2.compute.amazonaws.com:8080)
- **Swagger API 문서**: [http://ec2-43-203-68-225.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html](http://ec2-43-203-68-225.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html) 
- **GitHub Repository**: [https://github.com/sooyeoneo/baro-java](https://github.com/sooyeoneo/baro-java)
- **백엔드 직무 개발 과제(Java)**: [https://www.notion.so/Java-1f4c6e685f108008b773e2e0fb419af3?pvs=4](https://www.notion.so/Java-1f4c6e685f108008b773e2e0fb419af3?pvs=4)

---

## 🛠 기술 스택

### Back-end
- **Java 17 (OpenJDK)**: 메인 언어
- **Spring Boot 3.3.1**: 백엔드 프레임워크
- **Spring Security**: 인증 및 인가 처리
- **Spring Validation**: 유효성 검증
- **JJWT (io.jsonwebtoken)**: JWT 발급 및 검증

### Build & Deploy
- **Gradle**: 프로젝트 빌드 도구
- **Amazon EC2 (Ubuntu 22.04)**: 배포 서버 (프리 티어 사용)
- **Nginx**: 리버스 프록시 서버
- **환경 변수 기반 설정 (.properties)**: 민감 정보 보안 처리

### Test & Docs
- **JUnit 5**: 단위 테스트
- **Swagger (Springdoc OpenAPI)**: API 문서 자동화

---

## 🖥️ 주요 기능
- 회원가입 / 로그인
- JWT 발급 (Access + Refresh)
- 관리자 권한 부여
- Access Token 재발급

## 🌐 실행 방법
1. 빌드: `./gradlew build`
2. 실행: `java -jar build/libs/your-app.jar`
3. 접속: `http://localhost:8080/swagger-ui`

## 🔐 API 인증 방법
- Swagger 우측 상단 `[Authorize]` 클릭
- `Bearer <AccessToken>` 입력

## ⚙️ 주요 설정
- JWT 유효시간: Access 2시간 / Refresh 3일
- CORS, 예외 핸들링, 인증 필터 적용

## 📁 패키지 구조
- `auth`, `config`, `controller`, `dto`, `exception`, `jwt`, `model`, `repository`, `service`

```bash
baro-java/
┣ src/
┃ ┣ main/
┃ ┃ ┣ java/com/example/barojava/
┃ ┃ ┃ ┣ auth/ # 인증/인가 구현 (UserDetailsServiceImpl 등)
┃ ┃ ┃ ┣ config/ # Spring Security 및 Swagger 설정
┃ ┃ ┃ ┣ controller/ # API 컨트롤러
┃ ┃ ┃ ┣ dto/ # 요청 및 응답 DTO
┃ ┃ ┃ ┣ exception/ # 예외 처리 핸들러 및 커스텀 예외
┃ ┃ ┃ ┣ jwt/ # JWT 발급, 검증 로직
┃ ┃ ┃ ┣ model/ # Entity 클래스
┃ ┃ ┃ ┣ repository/ # 데이터베이스 접근 계층
┃ ┃ ┃ ┗ service/ # 비즈니스 로직 서비스 계층
┃ ┃ ┣ resources/
┃ ┃ ┃ ┣ static/ # 정적 리소스
┃ ┃ ┃ ┣ templates/ # 템플릿 (현재는 비어있음)
┃ ┃ ┃ ┗ application.properties # 설정 파일
┃ ┗ test/
┃ ┃ ┗ java/com/example/barojava/
┃ ┃ ┃ ┗ service/
┃ ┃ ┃ ┗ BaroJavaApplicationTests.java # 통합 테스트
┣ build.gradle
┣ gradlew
┣ gradlew.bat
┗ README.md
```

---

## 🔑 JWT 인증 테스트 방법

1. **회원가입**
   - `POST /auth/signup`
   - 요청: JSON 형식 { "username": "...", "password": "..." }

2. **로그인**
   - `POST /auth/login`
   - 성공 시, `Access Token`, `Refresh Token` 반환

3. **Access Token 만료 시 재발급**
   - `POST /auth/reissue`
   - `Authorization: Bearer <Refresh Token>` 헤더 포함

4. **관리자 권한 부여 API (예시)**
   - `PATCH /admin/users/{userId}/role`

---

## 🛥️ 배포 방법 요약 (수동 배포)

### 1. EC2 접속
```bash
ssh -i ~/Downloads/baro-key-pair.pem ubuntu@<EC2_PUBLIC_IP>
```

### 2. 환경 변수 설정
```bash
export JWT_SECRET=your-secret-value
```

### 3. 프로젝트 빌드
```bash
cd baro-java
./gradlew clean build -x test
```

### 4. 백그라운드 실행
```bash
cd build/libs
nohup java -jar baro-java-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### 5. Nginx 설정 예시 (/etc/nginx/sites-available/default)

```nginx
server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

--- 

## ☑️ 과제 요구사항 요약

1️⃣ **기능 구현**  
- JWT 기반 회원가입, 로그인 기능을 구현하고, 사용자와 관리자 권한(Role) 기반 접근 제어를 적용

2️⃣ **테스트 코드 작성**  
- JUnit 기반으로 회원가입, 로그인, 권한 부여 등에 대한 단위 및 통합 테스트를 작성

3️⃣ **API 명세 문서화 (Swagger)**  
- Swagger를 이용해 API 명세를 시각화하고 `/swagger-ui/index.html` 경로에서 확인 가능하도록 구성

4️⃣ **EC2 배포 및 리버스 프록시 구성**  
- Amazon EC2 서버에 애플리케이션을 배포하고, Nginx를 리버스 프록시로 설정하여 외부 접근이 가능
