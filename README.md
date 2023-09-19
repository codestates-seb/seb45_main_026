<div align="center">
<h2>Team. OppenHeimer</h2>
 <a href="https://www.itprometheus.net">
 <img alt="대표 이미지" src ="/images/itprometheus-logo.png" />
 </a>   
 <br>
<b>IT 교육 오픈 플랫폼</b><br>

 <h3 align="center">🧑‍💻 함께 성장하기 위한 선택, Prometheus</h3>
 
 프로젝트 기간: 2023.08.24 ~ 2023.09.22
</div>

## 🔗 배포주소 : [PROMETHEUS](https://www.itprometheus.net) 
- 게스트용 ID/PW : guest@gmail.com / 1q2w3e4r!
- 마음에 드신다면 `Star` 부탁드립니다! ㅎㅎ

## 🛫 소개
> 🧐 부담없이 IT 교육 동영상을 보고 올릴 수 있는 공간이 없을까?
> 가벼운 마음으로 볼 수 있는 IT 영상이 있었으면 좋겠다..

- 어떤 자격 조건이나 심사 없이 자신만의 IT 노하우를 간편하게 찍고 올릴 수 있습니다.
- 💸 무료 영상, 리워드 적립까지 다양한 혜택이 있어요.
- 구독, 카테고리 등으로 자신에게 알맞은 영상을 찾으세요!


---

# 📝 [목차](#index) <a name = "index"></a>
- [팀원 소개](#team)
- [기술 스택](#tech)
- [Idea](#idea)
- [개발 문서](#document)
- [아키텍처](#architecture)
- [서비스 페이지](#outputs)
- [[FE] 왜/어떻게 이 기술을 사용했나요?](#whyfe)
- [[BE] 왜/어떻게 이 기술을 사용했나요?](#whybe)
- [테스트/모니터링](#test)
- [리팩토링/성능 개선](#refactoring)
- [성과 및 회고](#retrospection)

# 👨‍👨‍👧‍👧 팀원 소개 <a name = "team"></a>
|[김혜림](https://github.com/HyerimKimm)|[방승환](https://github.com/BangSeung)|[이종범](https://github.com/JB0129)|[김호빈](https://github.com/hobeen-kim)|[김진아](https://github.com/oksu01)|[함예준](https://github.com/da9dac)|
|:---:|:---:|:---:|:---:|:---:|:---:|
|![김혜림](https://avatars.githubusercontent.com/u/50258232?v=4)|![방승환](https://avatars.githubusercontent.com/u/129879667?v=4)|![이종범](https://avatars.githubusercontent.com/u/130051470?v=4)|![김호빈](https://avatars.githubusercontent.com/u/102138860?v=4)|![김진아](https://avatars.githubusercontent.com/u/117355227?v=4)|![함예준](https://avatars.githubusercontent.com/u/102796152?v=4)|
|FE 부팀장|FE|FE|BE 팀장|BE|BE|

<details>
   <summary> FE 담당 파트 (👈 Click)</summary>
<br />
  
+ **김혜림(부팀장)**
  - 사용자 인증, OAuth, 회원정보 관리 기능 구현
  - 결제 목록, 리워드 목록 무한스크롤 적용
  - React hook form 라이브러리 적용
  - 시작 페이지 구현
  - 클린 코드를 고려한 Custom hook, 재활용 컴포넌트 구현, API 로직 분리
  - 디자인 시스템, storybook 적용
  - 클라이언트 배포 관리
<Br>

+ **방승환**
  - 영상 필터기능 구현
  - 검색기능 및 자동완성 구현
  - 강의 및 채널 목록  전반 구현 및 무한스크롤 적용
  - 채널페이지 구현
<Br>

+ **이종범**
  - S3 Presign-url를 통한 이미지 및 동영상 업로드
  - 강의 문제 CRD
  - 장바구니 리스트 CRUD
  - TossPayment 라이브러리 적용
  - React-Player 라이브러리 적용, 커스텀 컨트롤러 구현, 1분 미리보기
  - 리뷰 CRUD, 필터 기능, Pagination
  - 내 강의 활성화/비활성화 기능
<Br>
</details>

<details>
   <summary>BE 담당 파트 (👈 Click)</summary>
<br />
  
+ **김호빈(팀장)**
  - AWS 아키텍처 설계 및 환경 구축
  - Git Actions CI/CD 구축
  - Logging 및 Cloudwatch 모니터링
  - API Rest docs 문서화
  - 비디오 관련 기능, 주문/결제 기능, 채널 공지사항 기능
<Br>

 + **김진아**
   - 채널 기능
   - T비디오 리뷰 기능
   - 통합 테스트(채널, 리뷰, 공지사항)
<Br>

+ **함예준**
  - 로그인 및 회원가입, OAuth (인증 및 인가 기능) 기능
  - 회원 정보 관련 CRUD 기능
  - API Rest docs 문서화
  - 통합 테스트(회원, 비디오, 인증, 주문)
<Br>
</details>
<Br>

# 🔧 기술 스택 <a name = "tech"></a>

### Front-end

<img alt="HTML5" src ="https://img.shields.io/badge/HTML5-E34F26.svg?&style=for-the-badge&logo=HTML5&logoColor=white"/> <img alt="CSS3" src ="https://img.shields.io/badge/CSS3-1572B6.svg?&style=for-the-badge&logo=CSS3&logoColor=white"/> <img alt="JavaScript" src ="https://img.shields.io/badge/JavaScript-F7DF1E.svg?&style=for-the-badge&logo=JavaScript&logoColor=white"/> <img alt="React" src ="https://img.shields.io/badge/React-61DAFB.svg?&style=for-the-badge&logo=React&logoColor=white"/> <img alt="Redux Toolkit" src ="https://img.shields.io/badge/Redux Toolkit-764ABC.svg?&style=for-the-badge&logo=Redux&logoColor=white"/> <img alt="Axios" src ="https://img.shields.io/badge/Axios-5A29E4.svg?&style=for-the-badge&logo=Axios&logoColor=white"/> <img alt="styled components" src ="https://img.shields.io/badge/styled components-DB7093.svg?&style=for-the-badge&logo=styled-components&logoColor=white"/>
<img alt="React Hook Form" src ="https://img.shields.io/badge/React Hook Form-EC5990.svg?&style=for-the-badge&logo=react%20hook%20form&logoColor=white"/> <img alt="React Router" src ="https://img.shields.io/badge/React Router-CA4245.svg?&style=for-the-badge&logo=reactrouter&logoColor=white"/>   <img alt="Storybook" src ="https://img.shields.io/badge/Storybook-FF4785.svg?&style=for-the-badge&logo=Storybook&logoColor=white"/>

<br>

### Back-end
<img alt="Java" src ="https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white"/> 

<img alt="Spring Boot" src ="https://img.shields.io/badge/Spring Boot-6DB33F.svg?&style=for-the-badge&logo=springboot&logoColor=white"/> <img alt="jwt" src ="https://img.shields.io/badge/jwt-333333.svg?&style=for-the-badge&logo=jwt&logoColor=white"/> <img alt="jwt" src ="https://img.shields.io/badge/oauth-4479A1.svg?&style=for-the-badge&logo=springsecurity&logoColor=white"/> <img alt="JPA" src ="https://img.shields.io/badge/jpa-6DB33F.svg?&style=for-the-badge&logo=jpa&logoColor=white"/> <img alt="queryDsl" src ="https://img.shields.io/badge/querydsl-4479A1.svg?&style=for-the-badge&logo=querydsl&logoColor=white"/> <img alt="Spring Boot" src ="https://img.shields.io/badge/Rest Docs-6DB33F.svg?&style=for-the-badge&logo=readthedocs&logoColor=white"/> <img alt="Spring Boot" src ="https://img.shields.io/badge/Rest Docs-6DB33F.svg?&style=for-the-badge&logo=React Router&logoColor=white"/>

<img alt="mysql" src ="https://img.shields.io/badge/mysql-4479A1.svg?&style=for-the-badge&logo=mysql&logoColor=white"/> <img alt="Redis" src ="https://img.shields.io/badge/Redis-DC382D.svg?&style=for-the-badge&logo=redis&logoColor=white"/>

<br>

### Dev-Ops & Tool
<img alt="Git" src ="https://img.shields.io/badge/Git-F05032.svg?&style=for-the-badge&logo=Git&logoColor=white"/> <img alt="AWS" src ="https://img.shields.io/badge/AWS-232F3E.svg?&style=for-the-badge&logo=amazonaws&logoColor=white"/> <img alt="Docker" src ="https://img.shields.io/badge/Docker-4479A1.svg?&style=for-the-badge&logo=Docker&logoColor=white"/>

<br>

# 💡 아이디어 <a name = "idea"></a>

<details>
   <summary> 아이디어 확인 (👈 Click)</summary>
<br />

저희는 기존 교육 동영상 플랫폼의 문제점으로 생산자와 구매자의 역할이 나눠지고 쉽게 생산자가 될 수 없다는 점에 주목했습니다. 판매를 위한 교육 영상을 업로드하기 위해서는 일정 심사를 통과해야 하거나, 양질의 동영상을 여러 개 찍은 후 하나의 강의로 묶어야 하는 등 생산자로 진입하기 위한 장벽이 높았습니다.

따라서 이를 해결하고 누구나 생산자이자 소비자가 될 수 있도록 IT 교육 오픈 플랫폼을 제공하기 위해 서비스를 기획했습니다. 그리고 무료 동영상 업로드, 리뷰 작성, 문제 풀기 등의 활동으로 쉽게 리워드를 적립하고 활용할 수 있도록 유도하여 플랫폼의 이용이 선순환될 수 있도록 하였습니다.

</details>

<br>

# 📄 개발 문서 <a name = "document"></a>

- 사용자 요구사항 정의서 바로가기 ☞ [요구사항 정의서 파일](https://1drv.ms/x/s!AjqmbzT14UKwi0P6wFF3pjN1tkgw?e=mchNti)
- API 명세서 바로가기 ☞ [API 명세서 (restDocs)](https://api.itprometheus.net)
- Figma 바로가기 ☞ [Figma](https://www.figma.com/file/PXVOZwRkw6LmWof1HUL2q1/%ED%94%84%EB%A1%9C%EB%A9%94%ED%85%8C%EC%9A%B0%EC%8A%A4-%EC%9B%B9%EC%82%AC%EC%9D%B4%ED%8A%B8-%ED%99%94%EB%A9%B4-%EC%84%A4%EA%B3%84%EC%84%9C?type=design&node-id=0-1&mode=design&t=FxvRqxuBDV44mvy0-0)
- Storybook 배포 링크 바로가기 ☞ [Storybook](https://6509249be693180105e6fcc7-tgkycquvac.chromatic.com/?path=/docs/configure-your-project--docs)
- 개발자 테스트 문서 바로가기 ☞ [개발자 테스트 파일](https://1drv.ms/x/s!AjqmbzT14UKwi0404Ht2GZMTb-oD?e=Djm5VW)
- <details>
   <summary> ERD 보기 (👈 Click)</summary>
    <br />
 
     <img alt="ERD" src ="/images/erd.png"/>
    
    </details>
    
    <br>

# 📈 아키텍처 <a name = "architecture"></a>



<details>
   <summary> <b>아키텍처 상세 확인</b> (👈 Click)</summary>
    <br />
 
 <img alt="architecture" src ="/images/architecture.png"/>

### 웹 클라이언트
- S3 버킷에 웹 서버 배포
- CloudFront 와 Route53 을 통해 라우팅, HTTPS 서버 구축, 정적 파일 캐싱

### 애플리케이션 서버
- private subnet 에 외부와 격리하여 배포, NAT instance 로 아웃바운드 통신
- Load Balancer 와 Auto Scaling Group 으로 트래픽 분산, 가용성 확보

### 데이터 베이스
- RDS(MySQL) 이중화 구성, Redis Cluster 구축
  
### CloudWatch
- API, DB 응답 시간 확인
- 에러 로그 로깅
- ASG 메모리 및 CPU, 각 요청별 응답 시간 모니터링[대시보드 바로가기](https://cloudwatch.amazonaws.com/dashboard.html?dashboard=prometheus-dashboard&context=eyJSIjoidXMtZWFzdC0xIiwiRCI6ImN3LWRiLTU4OTc5NDU0NjI0NCIsIlUiOiJ1cy1lYXN0LTFfUXd0TzZvdWxRIiwiQyI6IjEyZ2E2M2NrNDZzMHBudTZqZTI4c25tamZ2IiwiSSI6InVzLWVhc3QtMTo4YTAyZDc0YS0zOTVjLTQxOGYtYjQzMS0wODQwODdlMGZhYzciLCJNIjoiUHVibGljIn0%3D&start=PT1H&end=null)
  
### 정적 리소스 파일
- 클라이언트가 직접 s3 버킷에 업로드 (presignedUrl 활용)
- CloudFront 로 파일 캐싱

</details>

<br>

# 📸 서비스 페이지 <a name = "outputs"></a>

<details>
   <summary> 서비스 페이지 확인 (👈 Click)</summary>
<br />

|시작 페이지|로그인 페이지|
|------|---|
|<img width="400px" alt="스크린샷 2023-09-18 오전 1 39 50" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/f9fc6c00-0646-446e-8bcf-f1eb241eb335">|<img width="400px" alt="스크린샷 2023-09-14 오후 2 49 40" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/15fabf7d-a5eb-4c09-a043-13b89e0e8e13">|

|회원가입 페이지|비밀번호 찾기 페이지|
|------|---|
|<img width="400px" alt="스크린샷 2023-09-14 오후 2 50 35" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/312ce2c2-41af-4ca3-8ba5-afb202ce8a98">|<img width="400px" alt="스크린샷 2023-09-14 오후 2 59 44" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/fb0de012-39e4-4631-9481-43875c02eaa4">|

|강의 목록 페이지|강의 상세 페이지|
|------|---|
|<img width="400px" alt="스크린샷 2023-09-18 오전 1 40 25" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/f1af15bb-49c0-4fea-ba87-ed572515a645">|<img width="400px" alt="스크린샷 2023-09-14 오후 2 53 21" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/ea065f95-f823-45a9-a270-ea4a0b3f6d01">|

|문제풀기 페이지|장바구니 페이지|
|------|---|
|<img width="400px" alt="스크린샷 2023-09-14 오후 2 56 37" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/8c59c502-93d7-4ac7-9f97-626dd456002c">|<img width="400px" alt="스크린샷 2023-09-14 오후 2 56 52" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/16ddf6e1-c662-4e11-8653-4c0779d8aa3a">|

|강의 업로드 페이지|문제 업로드 페이지|
|------|---|
|<img width="400px" alt="스크린샷 2023-09-15 오전 10 50 06" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/56c17a36-2b9f-462c-a782-ac39aca70a6c">|<img width="400px" alt="스크린샷 2023-09-14 오후 2 58 13" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/b4e2c9e5-c765-45f8-a8f2-556d51785e53">|

|채널 정보 페이지|구독 목록 페이지|
|------|---|
| <img width="400px" alt="스크린샷 2023-09-18 오전 1 41 22" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/b3eca375-fd56-47aa-af5b-016e86eb9f2d"> | <img alt="스크린샷 2023-09-15 오전 10 54 46" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/78c6d642-8135-4a09-a4f2-994a1ad83db9" width="400px"> |

|리워드 조회 페이지|결제 내역 페이지|
|------|---|
| <img width="400px" alt="스크린샷 2023-09-15 오전 11 00 53" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/d4d5a5e1-33cc-47dc-adcb-5e4b639d0b71"> | <img width="400px" alt="스크린샷 2023-09-18 오전 1 53 10" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/559c481f-fe13-45e9-9be0-48d5a4130e30"> |


|시청기록 페이지|
|------|
| <img width="400px" alt="스크린샷 2023-09-18 오전 1 46 48" src="https://github.com/codestates-seb/seb45_main_026/assets/50258232/363cadf0-65c9-4957-99c7-0e612fdab52c"> |

</details>

<br>

# ✨ [FE] 왜/어떻게 이 기술을 사용했나요? <a name = "whyfe"></a>

<details>
   <summary> FE 기술 상세 설명 확인 (👈 Click)</summary>
<br />

## React Hook Form
너무 많은 input state관리로 인한 코드의 가독성 저하와 잦은 화면 재랜더링을 해결하기 위해 사용했습니다. 
React Hook Form 은 input 입력값을 ref 형식으로 제어하여 화면의 재랜더링을 최소화하고, 
register, handleSubmit, formState, useWatch 등 다양한 메소드를 제공하여 
복잡한 유효성 검사를 간단하게 구현할 수 있도록 도와줍니다. 

## Custom Hook
반복 사용되는 로직을 재사용하기 위해 Custom Hook을 작성했습니다. 
Refresh Token을 이용한 Authorization 토큰 재발급 로직 재활용
useLogout, useLongPress useConfirm 등의 로직을 재활용 하기 위해 사용했습니다. 

## react-intersection-observer
무한 스크롤을 구현하기 위해서 사용했습니다. 
styled-component를 사용한 이번 프로젝트에서 ```useRef```나 ```addEventListener```등을 따로 설정할 필요 없이 ```useInView```로 간편하게 스크롤 바닥 감지 기능을 구현할 수 있습니다. 

## Redux Toolkit with Persistent
로그인 정보, 유저 정보, 다크모드 등 UI 설정 정보, 장바구니 정보, 재생 비디오 정보 등을 전역 상태로 관리하기 위해서 Redux Toolkit을 사용했습니다. Redux Toolkit을 사용하면 Redux에서 action 생성을 따로 작성해주지 않아도 된다는 장점이 있으며, persist를 사용해서 원하는 상태 정보를 로컬스토리지에 자동으로 저장되도록 설정할 수 있습니다. 

## react-player
기술 사진, 설명 등등

## Storybook & Figma Token
 👉🏻 <a href="https://6509249be693180105e6fcc7-tgkycquvac.chromatic.com/?path=/docs/configure-your-project--docs">storybook chromatic 배포 링크 바로가기</a>

## CSS keyframes
메인페이지의 Viewport 애니메이션과 롤링 배너, 사이드바 디자인을 위해서 styled-component에 keyframes를 적용하여 애니메이션을 구현했습니다.

## react-flicking
메인페이지 두 번째 페이지에 슬라이드 배너를 만들기 위해서 react-flicking UI 라이브러리를 적용했습니다. 

</details>

<br>

# ✨ [BE] 왜/어떻게 이 기술을 사용했나요? <a name = "whybe"></a>

<details>
   <summary> BE 기술 상세 설명 확인 (👈 Click)</summary>
<br />
 
## API 문서화
| 문서 메인 페이지 | API에 따른 상세 페이지 |
|-----|-----|
| <img width="400px" src="https://github.com/codestates-seb/seb45_main_026/assets/102796152/aa048ba0-557b-4cc9-bdb2-ea2cf0d23f8a"> | <img width="400px" src="https://github.com/codestates-seb/seb45_main_026/assets/102796152/b8509ec6-83b7-4707-8e56-9120e0c52b18"> |

Swagger 는 간단하고 빠르게 클라이언트에게 API 문서를 제공할 수 있습니다. 하지만 문서가 실제 API와 다르거나 성능을 보장 받지 못합니다.

따라서 이번 프로젝트에서는 검증된 API 의 제공과 깔끔한 API 로직 작성을 위해 Spring Rest Docs를 사용했습니다.

## OAuth
| 클라이언트 | 서버 |
|-----|---------|
| <img width="400px" src="https://github.com/codestates-seb/seb45_main_026/assets/102796152/cd733919-228b-4827-9a24-cd1564e8f22f"> | <img width="400px" src="https://github.com/codestates-seb/seb45_main_026/assets/102796152/1b0edb88-18dc-4ac1-9b4a-fe6fc3712fe7"> |

로컬 로크인 외에도 간편한 인증 방식 제공을 위해
구글, 카카오, 깃허브 로그인을 적용했습니다.

기존에 클라이언트가 OAuth 로그인을 요청하면 인증을 위한 리다이렉트 URI를 주었지만 클라이언트에게 미리 URI를 제공하여 바로 인가 코드를 전달 받아 중간에 리다이렉트 하는 과정을 생략했습니다.

## 이메일 인증 & Redis
| 이메일 전송 및 인증 | 인증된 경우 |
|-------------|---------|
| <img width="400px" src="https://github.com/codestates-seb/seb45_main_026/assets/102796152/21e36507-30c7-4337-a053-e9e85d58a20c"> | <img width="400px" src="https://github.com/codestates-seb/seb45_main_026/assets/102796152/8c8e132f-f3dc-4b3d-96a9-f58e7907655e"> |

일시적으로 인증과 관련된 정보를 저장하고 빠르게 처리하기 위해
키, 값 형태의 비정형 인메모리 데이터베이스인 Redis 를 사용했습니다.

사용자의 이메일을 키로 하고 인증 상태를 값으로 데이터를 저장한 후에 인증 코드 등으로 인증에 성공한 경우 인증 상태를 바꾸는 식으로 일시적인 인증이 필요한 경우에 적용했습니다.

## 검색기능 (Mysql fulltext)
| 데이터베이스 설정 및 인덱싱 | Full-Text Search |
|-------------|---------|
| <img width="400px" src="https://github.com/codestates-seb/seb45_main_026/assets/102796152/7e7022be-9111-404b-ae61-b28848fa74bf"> | <img width="400px" src="https://github.com/codestates-seb/seb45_main_026/assets/102796152/77271966-88ed-4e93-b95a-c3ca0f376862"> |
<br>
기존의 LIKE문은 쿼리는 문자열 패턴 매칭을 수행하여 대용량의 데이터베이스의 경우에 성능이 떨어질 수 있습니다. 따라서 텍스트 검색에 특화 되고 쉽게 사용할 수 있는 MySQL의 Full-Text 인덱스를 사용해서 검색 기능을 구현했습니다.

한글 검색에도 문제가 없는 n-gram parser를 사용하여 토큰의 값을 1로 설정해 인덱싱을 했기 때문에 최소 한 글자의 키워드로도 검색을 수행할 수 있게 했습니다.

## Querydsl
기본적인 CRUD 는 Spring Data JPA 가 기본적으로 제공해주는 CRUD 메서드 및 쿼리 메서드 기능을 사용했습니다. 하지만 GET 요청에서 원하는 조건의 데이터를 수집하기 위해서는 여러 조건이 필요합니다. 이때 쿼리문이 복잡해지기 때문에 JPQL 이나 Native Query 를 사용해야 하게 되는데, 쿼리문이 길어질 경우 오타나 문법적인 오류 등 휴먼 에러가 발생할 수 있습니다. 정적 쿼리가 아닌 이상 런타임 시점에 오류를 알 수 있게 됩니다. 따라서 위와 같은 문제를 해결하기 위해 Querydsl 을 부분적으로 사용했고 아래와 같은 이점이 있었습니다.

- Querydsl 은 컴파일 시점에 타입을 검사하기 때문에, 잘못된 필드명이나 데이터 유형 사용과 같은 오류를 빠르게 감지할 수 있습니다.
- 복잡한 조건을 기반으로 쿼리를 동적으로 생성할 수 있습니다. 이를 통해 동일한 메서드 내에서 다양한 조건 및 필터를 적용한 쿼리를 구성할 수 있습니다.
- Querydsl을 사용하면 쿼리가 실제 쿼리문처럼 작성되기 때문에 가독성이 좋습니다.

## git actions / Docker
 <img alt="gitactions" src ="/images/be/gitactions.png"/>
 
### Job 분리
 초기 git actions 는 하나의 Job 에서 모든 과정을 다 처리했습니다. 하지만 테스트가 무거워지고 그에 따라 빌드 시간이 오래 걸리면서 테스트와 빌드를 분리해야겠다는 판단이 들었습니다. git actions 의 Job 은 병렬로 실행되기 때문에 test 를 크게 4개(testA, testB, testC, buildTest) 로 나누고 각각 실행시켰습니다. 빌드 시에 API 문서화가 필요하기 때문에 ControllerTest 는 빌드 Job 에서 하도록 했습니다. 이후 모든 테스트와 빌드가 완료되면 cd Job 이 실행됩니다. 해당 Job 에서 도커를 push 하고 EC2 에서 도커 이미지를 다운로드 받아 배포하는 과정이 이루어집니다. 
 
### Caching
 `build.gradle` 파일은 자주 변경되지 않기 때문에 `actions/cache@v2` 을 통해 의존성을 캐싱해주어서 테스트 및 빌드 시간을 단축시켜주었습니다. 도커 이미지 캐싱이나 레이어 캐싱은 오히려 캐싱을 하기 위해 이미지를 불러오고 저장하는 과정이 시간이 더 걸려서 생략했습니다.
 
### Docker Image 최적화
멀티 스테이지를 생각했으나 이미 git actions 의 Job 에서 빌드와 배포가 분리되기 때문에 멀티 스테이지 빌드는 하지 않았습니다. 대신 도커 이미지 크기를 줄이기 위해 base-image 를 `openjdk:11-jre-slim` 로 변경했고, 애플리케이션의 `build.gradle` 에서 불필요한 dependency 를 제거했습니다.

### 결과
결과적으로 CICD 과정은 평균 3분 20초에서 2분으로 줄였고, 이미지 크기는 731MB 에서 296MB 로 60% 줄였습니다.

빌드 최적화 상세 내용 확인 ☞ [CI/CD](https://hobeen-kim.github.io/devops/Docker-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%B5%9C%EC%A0%81%ED%99%94/)

## 로그 및 모니터링
ec2 배포 이후 최초 몇 번의 GET 요청은 1000ms 이상의 시간이 걸린다는 걸 확인했습니다. 그리고 배포 환경과 로컬 환경도 분명히 차이가 있기 때문에 어떤 에러가 발생하는지도 모니터링이 가능해야 했습니다. 따라서 아래와 같은 로그 전략을 세웠습니다. 
### 로그 전략
 - API 호출 시 소요되는 시간
 - Repository 클래스 호출 시 소요되는 시간
 - 비즈니스 예외를 제외한 모든 Exception
### 로그 확인
로그는 logBack 을 사용했습니다. `@Slf4j` 어노테이션으로 적용할 수 있고, 로그를 파일로 남기는 게 간단하기 때문입니다. 또한 스프링 부트는 기본적으로 SLF4J를 사용하고 Logback을 그 구현체로 선택하고 있기 때문에 스프링 내부에서 예상치 못한 에러가 발생했을 때도 함께 로그가 남는다는 장점도 있습니다.

API 로그는 `MDCLoggingFilter` 에서, Data 접근 로그는 `RepositoryLoggingAop` 에서, 에러 로그는 `GlobalExceptionHandler` 에서 남겼습니다. `log.info` 혹은 `log.error` 가 발생하면 logs 폴더에 `.log` 파일로 기록됩니다. 해당 로그는 ec2 에서 cloudwatch agent 에 의해 수집되어 log group 에 기록됩니다. 그리고 metric 을 통해 파싱한 후 필요한 값은 대시보드에 표시했습니다.

[대시보드 바로가기](https://cloudwatch.amazonaws.com/dashboard.html?dashboard=prometheus-dashboard&context=eyJSIjoidXMtZWFzdC0xIiwiRCI6ImN3LWRiLTU4OTc5NDU0NjI0NCIsIlUiOiJ1cy1lYXN0LTFfUXd0TzZvdWxRIiwiQyI6IjEyZ2E2M2NrNDZzMHBudTZqZTI4c25tamZ2IiwiSSI6InVzLWVhc3QtMTo4YTAyZDc0YS0zOTVjLTQxOGYtYjQzMS0wODQwODdlMGZhYzciLCJNIjoiUHVibGljIn0%3D&start=PT1H&end=null)

특히 대시보드를 통해 GET 요청의 응답 시간 지연은 배포 시마다 발생한다는 걸 알 수 있었고 아래의 warm up 을 하게 되는 이유가 되었습니다.

## JVM warm up
 <img alt="warmup" src ="/images/be/warmup.png"/>
 
 warmup 을 하게 된 이유는 위 그래프와 같이 특정 요청에서 1000ms 이상의 레이턴시를 발견했기 때문입니다. 처음에는 GET 요청에서 쿼리문의 조건이 많이 걸려있어서 그런가 생각했지만 실제로 DB 에서 쿼리문 실행 자체는 10ms 이 채 되지 않았습니다. 로그를 통해 확인해본 결과 오히려 애플리케이션 로직을 실행하는 데 대부분의 시간이 소요되었습니다. 따라서 JVM 의 JIT(Just-in-Time) 으로 인해 컴파일이 실시간으로 되기 때문에 발생한 문제라고 생각했습니다. 그 이유는 최초 몇 개의 요청 이후에는 정상적인 응답 시간을 반환했기 때문입니다.

처음에는 애플리케이션이 실행되면서 localhost:8080 으로 GET 위주의 메인 API 를 호출하면서 warm up 과정을 실행했습니다. 하지만 warm up 과정이 3분 이상 소요되면서 메인 로직 메서드를 호출하는 방향으로 바꾸었습니다. 그리고 스프링의 필터 부분도 warm up 하기 위해 `WarmupController` 를 만들어 해당 API 를 함께 호출해주었습니다. 아래는 warmup 을 하는 클래스입니다.
```java
public class WarmupApi implements ApplicationListener<ContextRefreshedEvent> {

    ... 

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (warmup 이 되지 않았다면) {
;
            request("http://localhost:8080/warmup"); //WarmupController 호출
            methodWarmup(); //메서드 단위 warmup 수행

            warmupState.setWarmupCompleted(true); //warmup 을 완료 상태로 변경
        }
    }

    private void methodWarmup() {

        videoMethodWarmup();
        channelMethodWarmup();
        memberMethodWarmup();
    }

    ...
}
```
해당 과정을 완료하면 최초 요청 시에도 평균적인 응답 시간 (200ms ~ 400ms) 으로 요청이 되는 것을 확인할 수 있었습니다.

warm up 상세 내용 확인 ☞ [warm up](https://hobeen-kim.github.io/java/JAVA-JIT-%EC%B5%9C%EC%A0%81%ED%99%94)

</details>

<br>

# 🔨 테스트/모니터링 <a name = "test"></a>

<details>
   <summary> 테스트/모니터링 확인 (👈 Click)</summary>
<br />

## 개발자 테스트

개발자 테스트는 요구사항 기반으로 테스트를 진행했습니다. 요구사항에는 없지만 필요한 내용이나 아이디어는 스크럼을 통해 테스트 문서에 추가했습니다. 개발부터 배포까지 정기적으로 테스트를 진행했으며 테스트 통과를 중심으로 기능 개발을 하였습니다.

- 개발자 테스트 문서 바로가기 ☞ [개발자 테스트 파일](https://1drv.ms/x/s!AjqmbzT14UKwi0404Ht2GZMTb-oD?e=Djm5VW)

## BE 단위 및 통합테스트

 <img alt="test" src ="/images/be/test.png"/>

 BE JAVA 애플리케이션에서 총 754 개의 단위 및 통합테스트를 진행했습니다. 단위 테스트의 대상은 Controller, Service, Repository, Entity 의 모든 public 메서드입니다. 중점은 기능 동작 여부와 엣지 케이스에서 정상적인 예외를 던지는지 였습니다. 통합테스트는 주어진 조건에서 mockMvc 를 활용해 API 를 호출했을 때 기대되는 응답값과 DB CRUD 가 되는지 확인했습니다.

</details>

<br>

# ♻️ 리팩토링/성능 개선 <a name = "refactoring"></a>

<details>
   <summary> 리팩토링/성능 개선 확인 (👈 Click)</summary>
<br />

## 리팩토링1
기술 사진, 설명 등등

## 성능개선1
기술 사진, 설명 등등

</details>

<br>

# 📌 회고 <a name = "retrospection"></a>

<details>
   <summary> 회고 확인 (👈 Click)</summary>
<br />

- 김혜림

- 방승환

- [이종범](https://dunggu.tistory.com/66)

- 김호빈

- 김진아

- 함예준

</details>

<br>
