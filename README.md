# compose-board-app

Jetpack Compose로 구현한 게시판 안드로이드 앱입니다.
Kotlin + Spring Boot API 서버([kotlin-board-api](#연동-프로젝트))와 연동됩니다.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02.01-4285F4?logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-3.0.0-48B983)
![Gradle](https://img.shields.io/badge/Gradle-9.4.1-02303A?logo=gradle&logoColor=white)

---

## 프로젝트 소개

게시글의 생성·조회·수정·삭제(CRUD)를 제공하는 안드로이드 클라이언트입니다.
Compose로 UI를 선언적으로 구성하고, Navigation Compose로 화면을 전환하며,
Retrofit을 통해 REST API 서버와 통신합니다.

**주요 관심사**

- Compose 선언형 UI와 상태 기반 화면 구성
- Navigation Compose를 이용한 화면 전환과 상태 호이스팅
- Retrofit `suspend` 함수를 통한 비동기 HTTP 통신

---

## 연동 프로젝트

| 리포지토리 | 역할 | 기술 |
|---|---|---|
| **compose-board-app** (현재) | 안드로이드 클라이언트 | Kotlin, Jetpack Compose, Retrofit |
| [kotlin-board-api](https://github.com/<your-github-id>/kotlin-board-api) | REST API 서버 | Kotlin, Spring Boot, JPA |

> 두 리포지토리는 별도로 관리되며 **HTTP/JSON 계약으로만 연결**됩니다.
---

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어 | Kotlin 2.2.10 |
| UI | Jetpack Compose (BOM 2026.02.01), Material 3 |
| 화면 전환 | Navigation Compose 2.9.2 |
| 네트워크 | Retrofit 3.0.0, Gson Converter 2.9.0 |
| 비동기 | Kotlin Coroutines |
| 빌드 | Gradle 9.4.1, AGP 9.2.1 |
| 지원 SDK | minSdk 24 / targetSdk 36 |

---

## 화면 구성

| 구성 요소 | 책임 |
|---|---|
| `MainActivity` | 앱 진입점, Compose 트리 및 테마 설정 |
| `Neo4App` | `NavHost` 구성, 화면 간 라우팅과 게시글 ID 전달 |
| `BoardListScreen` | 게시글 목록 조회 및 표시 |
| `ShowBoardScreen` | 게시글 단건 조회, 수정·삭제 메뉴 |
| `CreateBoardScreen` | 게시글 작성 폼 |
| `UpdateBoardScreen` | 게시글 수정 폼 |
| `Board` | 목록 아이템 컴포넌트 |

화면 경로는 `Navigate` enum으로 관리합니다 — `BOARD_LIST`, `READ`, `CREATE`, `UPDATE`

---

## 서버 연동

`RetrofitBuilder`가 Retrofit 인스턴스와 `BoardService`를 제공하며,
모든 통신 메서드는 코루틴 `suspend` 함수로 선언되어 있습니다.

| BoardService | Method | Endpoint | 설명 |
|---|---|---|---|
| `createBoard` | `POST` | `/board` | 게시글 생성 |
| `getBoardList` | `GET` | `/board` | 전체 조회 |
| `getBoardById` | `GET` | `/board/{id}` | 단건 조회 |
| `updateBoard` | `PATCH` | `/board` | 게시글 수정 |
| `deleteBoard` | `DELETE` | `/board/{id}` | 게시글 삭제 |

### RequestBoard

서버의 `BoardDto`와 필드가 1:1로 대응하며, Gson이 JSON을 변환합니다.

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | `Int` | 게시글 ID |
| `title` | `String` | 제목 |
| `content` | `String` | 내용 |
| `name` | `String` | 작성자 |

---

### 실행 설정

| 항목 | 위치 |
|---|---|
| 서버 주소 | `RetrofitBuilder.getRetrofit()` 의 `baseUrl` |
| 인터넷 권한 | `AndroidManifest.xml` · `android.permission.INTERNET` |
| 평문 HTTP 허용 | `AndroidManifest.xml` · `usesCleartextTraffic="true"` |


> **서버 주소 설정**
> 에뮬레이터·실기기는 `localhost`를 인식하지 못합니다.
> 서버가 실행 중인 PC의 **LAN IP**(예: `http://???.???.?.?:8080`)를
> `baseUrl`에 지정하고, 서버 PC의 방화벽에서 8080 포트 인바운드를
> 허용해야 합니다.


---
