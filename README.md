# 🍽️ Let's Eat

**Let's Eat**는 아동급식카드를 활용한 마을 탐험 게임형 식사 예약 서비스입니다.  
MSA(Microservices Architecture) 기반으로 서비스가 독립적으로 배포 및 확장 가능하도록 설계되었습니다.
SpringBoot 기반 백엔드 레포지토리입니다.

<div align="center">
<img width="70%" src="https://github.com/user-attachments/assets/ece2f279-d183-42fb-97d2-0faf97c1178e" />
</div>

## 🛠 Tech Stack
<img src="https://skillicons.dev/icons?i=spring,mysql,redis,docker,kubernetes,github" height="50">

---

## 📚 목차

1. [프로젝트 소개](#-프로젝트-소개)  
2. [기술 스택](#-기술-스택)  
3. [팀원 소개](#-팀원-소개)  
4. [서비스 아키텍처](#-서비스-아키텍처)  
5. [프로젝트 구조](#-프로젝트-구조)
6. [쿠버네티스 설정](#-쿠버네티스-설정) 
7. [서비스 간 통신](#-서비스-간-통신)  
8. [CI/CD 파이프라인](#-cicd-파이프라인)  
9. [MSA 아키텍처](#-msa-아키텍처)

---

## 📖 프로젝트 소개

**Let's Eat**는 아동급식카드 사용의 접근성을 높이고, 영양 균형 관리를 게임화하여 아이들이 즐겁게 식사할 수 있도록 돕는 서비스입니다.  

**주요 특징**
- 게임형 마을 인터페이스로 직관적인 가게 탐색  
- 디지털 초대장 시스템으로 오프라인 결제 간소화  
- OpenFeign 기반 서비스 간 동기 통신  
- Kubernetes 환경에서의 안정적인 MSA 운영

<div align="center">

<img width="70%" src="https://github.com/user-attachments/assets/ea465397-9bd9-4470-bad5-e659abb6119e" />

<img width="70%" src="https://github.com/user-attachments/assets/06bfded3-65e0-43c2-b09a-330b5e7b976e" />

<img width="70%" src="https://github.com/user-attachments/assets/1fb615af-8b7d-4175-b229-a7132131f103" />

<img width="70%" src="https://github.com/user-attachments/assets/d9dde671-4c44-41b5-99ba-cdbe3260de84" />

<img width="70%" src="https://github.com/user-attachments/assets/42adc30d-b362-4775-9083-75e66cf61f98" />

<img width="70%" src="https://github.com/user-attachments/assets/9160caa2-81f0-40cd-a077-9394332363c2" />

<img width="70%" src="https://github.com/user-attachments/assets/77883950-92dd-4a0d-93eb-3063ba1338c0" />

<img width="70%" src="https://github.com/user-attachments/assets/52c3f07e-9f64-4cac-803e-13ab4fc5b890" />

<img width="70%" src="https://github.com/user-attachments/assets/41a3a404-3c74-41f2-ab85-9bb383f965a9" />

<img width="70%" src="https://github.com/user-attachments/assets/f20e5aba-1bf1-4723-8f46-da6d69825565" />

<img width="70%" src="https://github.com/user-attachments/assets/d6d668aa-18dd-43df-8873-fd62ceb71f30" />

<img width="70%" src="https://github.com/user-attachments/assets/e929580e-9aac-4c48-8bd0-b2673106dbb2" />

<img width="70%" src="https://github.com/user-attachments/assets/9b8c717f-7cd6-4a84-823e-0615d0a97574" />

<img width="70%" src="https://github.com/user-attachments/assets/eb1f296b-6872-423e-a983-cd6ab14e4a22" />

<img width="70%" src="https://github.com/user-attachments/assets/f0afe463-212c-47a8-9a88-05912fb385af" />

<img width="70%" src="https://github.com/user-attachments/assets/cd735592-df96-40f0-8e6c-30c60c70abfe" />

<img width="70%" src="https://github.com/user-attachments/assets/29ed7149-4078-49ec-aab5-c1e4b589842d" />

</div>

---

## 🛠 기술 스택

| 구분 | 기술 |
|------|------|
| **Language** | Java |
| **Framework** | Spring Boot, Spring Cloud |
| **Database** | MySQL, Redis |
| **Infra** | Docker, Kubernetes(NKS), Naver Cloud Platform |
| **CI/CD** | NCP SourceCommit, SourceBuild, SourceDeploy, SourcePipeline |
| **Auth** | JWT, Spring Security |
| **Communication** | OpenFeign |
| **외부 API** | Google Vision API, NCP Object Storage |

---

## 👤 팀원 소개

<div align="center">

| Team Member | Team Member |
|:---------------------------------------------------------------------:|:-------------------------------------------------------------------:|
| <img src="https://github.com/JiwonLee42.png" width="200" /> | <img src="https://github.com/2anizirong.png" width="200" /> |
| [이지원](https://github.com/JiwonLee42) | [김이안](https://github.com/2anizirong) |
| • User/Pay Service 개발<br>• Gateway Service/JWT 인증 구현<br>• NKS 클러스터 구성 및 K8S 리소스 관리<br>• CI/CD 파이프라인 구축 | • Store/Review Service 개발<br>• OpenFeign 통신 구현<br>• NCP Object Storage 연동<br> |

</div>

---

## 🖥 서비스 아키텍처

<div align="center">
<img width="90%" src="https://github.com/user-attachments/assets/f8d583da-ce49-42ee-83db-6e8f4031142d" />
</div>

---

## 📁 프로젝트 구조
```plaintext
pairmate_backend/
├── common-libs/                    # 공통 라이브러리
├── config-server/                  # Config Server
├── eureka-server/                  # Eureka Server
├── gateway-service/                # API Gateway
├── user-service/                   # User Service
├── store-service/                  # Store Service
├── pay-service/                    # Pay Service
├── review-service/                 # Review Service
│
└── k8s/                            # Kubernetes 리소스
    ├── namespace.yaml
    ├── cert/                       # TLS 인증서
    ├── configmap/                  # 환경 설정
    ├── secret/                     # 민감 정보
    ├── db/                         # MySQL
    ├── redis/                      # Redis
    ├── deployment/                 # 서비스 배포
    ├── frontend/                   # Frontend
    └── ingress/                    # 라우팅
```

---

## ☸️ 쿠버네티스 설정

### 1. 네임스페이스

| 환경 | 네임스페이스 | 용도 |
|------|-------------|------|
| 테스트 | `pairmate-test` | 개발/테스트 환경 |
| 운영 | `pairmate-prod` | 프로덕션 환경 |
```bash
kubectl create namespace pairmate-test
kubectl create namespace pairmate-prod
```

---

### 2. Secret

| Secret 이름 | 내용 |
|-------------|------|
| `ncr-secret` | NCR 이미지 Pull 인증 |
| `config-git-secret` | Config Server Git 인증 |
| `redis-secret` | Redis 비밀번호 |
| `*-db-secret` | 각 서비스 DB 계정 정보 |
| `pay-pg-secret` | 결제 API 키 |
```bash
kubectl apply -f k8s/secret/ -n pairmate-prod
```

---

### 3. ConfigMap

| ConfigMap 이름 | 내용 |
|---------------|------|
| `global-config` | Config Server URL, 공통 환경 변수 |
| `nginx-config` | Nginx 설정 (API 프록시) |
```bash
kubectl apply -f k8s/configmap/ -n pairmate-prod
```

---

### 4. 인증서 및 Ingress

| 항목 | 설정 |
|------|------|
| **인증서** | Let's Encrypt (자동 갱신) |
| **운영 도메인** | lets-eat.store |
| **테스트 도메인** | test.lets-eat.store |
| **TLS** | TLS 1.3 |

**경로별 라우팅**

| 경로 | 서비스 |
|------|--------|
| `/` | frontend-service |
| `/api/auth` | user-service |
| `/api/stores` | store-service |
| `/api/review` | review-service |
| `/api/pay` | pay-service |

---

### 5. 운영 환경

| 구성 요소 | 설정 |
|----------|------|
| **플랫폼** | Naver Cloud Kubernetes Service (NKS) |
| **이미지 레지스트리** | Naver Container Registry (NCR) |
| **스토리지** | ncloud-block-storage |
| **로드밸런서** | NCP Load Balancer |

---

### 6. 배포 전략

| 전략 | 내용 |
|------|------|
| **Rolling Update** | 무중단 배포, 새 버전 기동 확인 후 전환 |
| **Topology Spread** | Pod 노드 간 균등 분산 |
| **Startup Probe** | 초기 구동 시간 확보 |
| **Readiness Probe** | 트래픽 수신 준비 확인 |
| **Liveness Probe** | 비정상 Pod 자동 재시작 |

---

### 7. 배포 명령
```bash
# DB 및 Redis 배포
kubectl apply -f k8s/db/ -n pairmate-prod
kubectl apply -f k8s/redis/ -n pairmate-prod

# 서비스 배포
kubectl apply -f k8s/deployment/ -n pairmate-prod

# 배포 상태 확인
kubectl rollout status deployment/<서비스명> -n pairmate-prod
```

---

## 🔗 서비스 간 통신

### OpenFeign 기반 동기 통신

| 순서 | 단계 | 설명 |
|------|------|------|
| 1 | **메서드 호출** | A 서비스에서 FeignClient 인터페이스 메서드 호출 |
| 2 | **호출 가로채기** | OpenFeign이 `@FeignClient` 정보 확인 |
| 3 | **서비스 탐색** | Eureka Server에서 B 서비스 IP/포트 조회 |
| 4 | **HTTP 요청 생성** | 메서드 매핑 정보 기반 HTTP 요청 생성 |
| 5 | **요청 전송** | B 서비스로 요청 전송 및 JSON 응답 수신 |
| 6 | **자동 변환** | JSON → ResponseDto 객체 자동 변환 |

**주요 특징**
- 서비스명 기반 통신 (IP 하드코딩 불필요)
- 자동 로드 밸런싱
- Circuit Breaker 적용
- 선언적 HTTP 클라이언트

---

## ⚙️ CI/CD 파이프라인

### NCP SourcePipeline

| 단계 | 도구 | 역할 |
|------|------|------|
| **1. 코드 관리** | SourceCommit | Git 저장소 관리, 브랜치 전략 |
| **2. 빌드** | SourceBuild | Docker 이미지 빌드, NCR 푸시 |
| **3. 배포** | SourceDeploy | NKS 클러스터 배포, Rolling Update |
| **4. 통합** | SourcePipeline | 전체 과정 자동화 및 모니터링 |

### 배포 프로세스
```
코드 푸시 → Docker 빌드 → NCR 저장 → K8S 배포 → 헬스체크 → 서비스 반영
```

### 환경 분리

| 환경 | 네임스페이스 | 도메인 |
|------|-------------|--------|
| 테스트 | pairmate-test | test.lets-eat.store |
| 운영 | pairmate-prod | lets-eat.store |

---

## 🏗 MSA 아키텍처

### 핵심 인프라

| 컴포넌트 | 역할 |
|---------|------|
| **Eureka Server** | 서비스 등록/탐색, 동적 라우팅 |
| **Config Server** | 중앙 설정 관리, 환경별 설정 분리 |
| **Gateway Service** | JWT 인증, CORS 처리, 라우팅, 로깅 |
| **Common Libs** | 공통 응답/예외 구조, JWT 유틸 |

---

### Application 서비스

| 서비스 | 주요 기능 |
|--------|----------|
| **User Service** | 회원가입, 로그인, JWT 발급, RefreshToken 관리 |
| **Store Service** | 가게 등록/수정/삭제, 카테고리 관리, 이미지 업로드 |
| **Review Service** | 리뷰 작성/수정/삭제, 별점 계산, 이미지 업로드 |
| **Pay Service** | 카드 등록/검증, 결제 처리, 한도 관리 |

---

### 보안 및 인증

| 항목 | 구현 |
|------|------|
| **인증** | JWT 기반 무상태 인증 |
| **토큰 저장** | RefreshToken → Redis |
| **통신 보안** | TLS 1.3, Let's Encrypt |
| **민감 정보** | Kubernetes Secret (Base64) |
