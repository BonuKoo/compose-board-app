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
| [kotlin-board-api](https://github.com/BonuKoo/kotlin-board-api) | REST API 서버 | Kotlin, Spring Boot, JPA |

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
| 서버 주소 | `local.properties` · `server.base.url` |
| 인터넷 권한 | `AndroidManifest.xml` · `android.permission.INTERNET` |
| 평문 HTTP 허용 | `AndroidManifest.xml` · `usesCleartextTraffic="true"` |


> **서버 주소 설정**
> 에뮬레이터·실기기는 `localhost`를 인식하지 못합니다.
> `local.properties`에 서버가 실행 중인 PC의 **LAN IP**를 지정하고,
> 서버 PC의 방화벽에서 8080 포트 인바운드를 허용해야 합니다.
> 이 파일은 버전 관리에서 제외되므로 개발 환경마다 값을 따로 둡니다.
> 값이 없으면 에뮬레이터 기본 주소(`http://10.0.2.2:8080`)를 사용합니다.
>
> `server.base.url=http://???.???.?.?:8080`

값은 빌드 시 `BuildConfig.SERVER_BASE_URL`로 주입되어 `RetrofitBuilder`가 읽습니다.


---

## 개선 이력

- 부수효과
- 네비게이션
**수정한 버그**

| 증상 | 원인 | 해결 |
|---|---|---|
| 화면 진입 시 조회 요청이 2회 발생. 상세 화면은 더보기 메뉴를 여닫을 때마다 요청이 추가로 발생 | `SideEffect`는 재구성이 일어날 때마다 실행된다 | `LaunchedEffect`로 교체. 진입 시 한 번만 실행되고 화면을 벗어나면 자동 취소된다 |
| 게시글을 삭제한 뒤 시스템 뒤로가기를 누르면 삭제된 글의 상세 화면으로 이동 (서버 500) | 뒤로가기까지 `navigate()`로 처리해 백스택에 화면이 계속 쌓였다 | 백스택에 이미 있는 경로면 `popBackStack`으로 되돌아가도록 변경 |
| 게시글 수정 화면 상단에 "게시글 생성"이 표시됨 | 생성 화면을 복사하며 남은 흔적 | "게시글 수정"으로 수정 |
| 더보기 메뉴가 의도한 위치에서 벗어나 열림 | `DropdownMenu`가 48dp 고정 크기인 `IconButton`의 content 슬롯 안에 있었다 | `Box`의 형제로 배치 |
| 개발 PC의 LAN IP가 바뀌면 모든 요청이 실패하고, 앱을 다시 빌드해야 복구됨 | `baseUrl`이 소스에 하드코딩되어 있었다 | `local.properties` 값을 `BuildConfig`로 주입 |

**구조 변경**

| 대상 | 변경 전 | 변경 후 |
|---|---|---|
| 데이터 로딩 | `SideEffect` / `DisposableEffect` 혼용 | `LaunchedEffect`로 통일 |
| 화면 전환 | 각 화면의 콜백이 `navController.navigate()`를 그대로 호출 | `Neo4App.moveTo()` 한 곳으로 집약 |
| 서버 주소 | `RetrofitBuilder` 내 문자열 상수 | `local.properties` → `BuildConfig.SERVER_BASE_URL` |

**동작 변경**

| 상황 | 변경 전 | 변경 후 |
|---|---|---|
| 목록 화면 진입 | GET `/board` 2회 | 1회 |
| 상세 화면에서 메뉴 열기 | 열 때마다 GET `/board/{id}` 추가 발생 | 요청 없음 |
| 게시글 삭제 후 뒤로가기 | 삭제된 글의 상세 화면 (오류 표시) | 앱 종료 |
| 수정 완료 후 상세 화면 복귀 | 새 화면을 쌓으며 재조회 | 백스택을 되감으며 재조회 (결과 동일, 스택은 정리됨) |

조회는 `LaunchedEffect(id)`를 사용해 게시글 ID가 바뀌면 다시 수행합니다.
백스택을 되감아 상세 화면으로 돌아올 때도 화면이 새로 구성되므로 수정된 내용이 반영됩니다.

에뮬레이터에서 실제 서버와 연동해 위 동작을 모두 확인했습니다.

### Phase 2 — 데이터 계층 분리

화면이 Retrofit 을 직접 호출하던 구조를 걷어내고 `BoardRepository`를 두었습니다.
**사용자가 보는 동작은 달라지지 않습니다.** 이후 단계에서 ViewModel 을 도입하기 위한 준비입니다.

**구조 변경**

| 대상 | 변경 전 | 변경 후 |
|---|---|---|
| 데이터 접근 | 각 화면이 `RetrofitBuilder.getBoardService()` 를 직접 호출 | `BoardRepository` 를 거침 |
| 모델 | `RequestBoard` 하나로 통신·화면 표시를 겸용 | `RequestBoard`(통신) / `Board`(화면) 로 분리 |
| 모델 변환 | 없음 | `BoardRepository` 가 담당 |
| 게시글 생성 | 화면이 의미 없는 `id = 0` 을 만들어 전달 | `create(title, content, writer)` — 화면은 id 를 다루지 않음 |
| 게시글 수정 | 화면이 `RequestBoard` 를 새로 조립 | `board.copy(title, content)` |

```
변경 전   화면 ──────────────────────────> BoardService (Retrofit)
변경 후   화면 ──> BoardRepository ──────> BoardService (Retrofit)
                        │
                   RequestBoard ↔ Board 변환
```

**얻은 것**

| 항목 | 내용 |
|---|---|
| 화면의 책임 축소 | 화면은 Retrofit 도 통신 모델도 알지 못한다 |
| 변경 범위 격리 | 서버 응답 형식이 바뀌어도 `BoardRepository` 만 고치면 된다 |
| 다음 단계 준비 | ViewModel 이 `BoardRepository` 만 주입받으면 되고, 테스트에서는 가짜 구현으로 교체할 수 있다 |

서버 쪽 `kotlin-board-api` 가 `BoardEntity` 와 `BoardDto` 를 분리한 것과 같은 이유입니다.

**동작 변경**

없습니다. 순수 구조 변경이며, 화면 흐름과 요청 횟수는 Phase 1 과 동일합니다.
에뮬레이터에서 목록 조회 · 단건 조회 · 생성 · 수정 · 삭제 5개 흐름이
모두 이전과 같이 동작하는 것을 확인했습니다.


---
