# 개선 기록

초기 구현은 Composable 하나가 UI 그리기 · 상태 보관 · 네트워크 호출을 모두 맡는 구조였다.
계층을 나누고 그 과정에서 드러난 결함을 고친 기록이다. 각 단계가 끝날 때마다 앱은 동작하는 상태를 유지했다.

| 단계 | 내용 |
|---|---|
| 1 | 부수효과 API와 네비게이션 |
| 2 | 데이터 계층 분리 |
| 3 | ViewModel과 화면 상태 |
| 4 | 의존성 주입과 단위 테스트 |
| — | 프로젝트 식별자 정리 |

---

## 1. 부수효과 API와 네비게이션

구조는 그대로 두고 눈에 보이는 결함부터 고쳤다.

### 수정한 버그

| 증상 | 원인 | 해결 |
|---|---|---|
| 화면 진입 시 조회 요청 2회 발생. 상세 화면은 메뉴를 여닫을 때마다 요청 추가 | `SideEffect`가 재구성마다 실행 | `LaunchedEffect`로 교체 |
| 삭제 후 시스템 뒤로가기를 누르면 삭제된 글의 상세 화면으로 이동 (서버 500) | 뒤로가기까지 `navigate()`로 처리해 백스택이 누적 | 백스택에 있는 경로면 `popBackStack` |
| 수정 화면 상단에 "게시글 생성" 표시 | 생성 화면 복사 흔적 | 문구 수정 |
| 더보기 메뉴가 어긋난 위치에 열림 | `DropdownMenu`가 48dp `IconButton`의 content 슬롯 안 | `Box`의 형제로 배치 |
| PC의 LAN IP가 바뀌면 전체 요청 실패, 재빌드해야 복구 | `baseUrl` 하드코딩 | `local.properties` → `BuildConfig` 주입 |

### 동작 변화

| 상황 | 이전 | 이후 |
|---|---|---|
| 목록 진입 | GET `/board` 2회 | 1회 |
| 상세에서 메뉴 열기 | 열 때마다 요청 추가 | 요청 없음 |
| 삭제 후 뒤로가기 | 삭제된 글의 오류 화면 | 앱 종료 |

---

## 2. 데이터 계층 분리

화면이 Retrofit을 직접 호출하던 연결을 끊었다. 사용자가 보는 동작은 바뀌지 않는다.

```
이전   화면 ─────────────────────> BoardService (Retrofit)
이후   화면 ──> BoardRepository ──> BoardService (Retrofit)
                     │
                RequestBoard ↔ Board
```

| 대상 | 이전 | 이후 |
|---|---|---|
| 데이터 접근 | 각 화면이 `getBoardService()` 직접 호출 | `BoardRepository` 경유 |
| 모델 | `RequestBoard` 하나로 통신·표시 겸용 | `RequestBoard`(통신) / `Board`(화면) |
| 게시글 생성 | 화면이 의미 없는 `id = 0`을 만들어 전달 | `create(title, content, writer)` |
| 게시글 수정 | 화면이 `RequestBoard`를 새로 조립 | `board.copy(title, content)` |

서버가 `BoardEntity`와 `BoardDto`를 나눈 것과 같은 이유다.

---

## 3. ViewModel과 화면 상태

상태를 화면 밖으로 옮기고, 화면이 가질 수 있는 상태를 타입으로 명시했다.

```
이전   화면(상태 보관 + 그리기) ──> Repository
이후   화면(그리기) <── StateFlow ── ViewModel ──> Repository
```

### 수정한 버그

| 증상 | 원인 | 해결 |
|---|---|---|
| 회전하면 목록이 사라지고 재조회 | 상태를 `remember`로 보관 | `ViewModel`로 이동 |
| 상세·수정 화면에서 회전하면 조회 실패 | 게시글 ID를 `remember`로 보관해 회전 시 0이 됨 | `rememberSaveable` |
| 등록 버튼 연타 시 누른 횟수만큼 생성 | 전송 중인지 아는 주체가 없음 | `isSubmitting`으로 비활성화 |
| 조회 실패 후 저장하면 서버 500 | ID가 0인 채로 전송 | 조회 성공 시에만 저장 활성화 |
| 로딩·빈 목록·실패가 모두 같은 빈 화면 | 성공 데이터만 상태로 보관 | `sealed interface`로 상태 구분 |
| 화면을 벗어나도 통신 코루틴 잔존 | `MainScope()` 직접 생성 | `viewModelScope` |

### 동작 변화

| 상황 | 이전 | 이후 |
|---|---|---|
| 목록에서 회전 | 빈 화면 후 재조회 | 목록 유지 |
| 상세에서 회전 | 조회 실패 | 게시글 유지 |
| 작성 중 회전 | 입력 내용 소실 | 입력 유지 |
| 등록 버튼 연타 | 누른 횟수만큼 생성 | 첫 요청만 처리 |
| 로딩 중 / 글 없음 / 연결 실패 | 모두 빈 화면 | 진행 표시 / 안내 문구 / 재시도 버튼 |

상태를 받는 부분과 그리는 부분을 나눈 결과, Preview가 서버 없이 네 가지 상태를 렌더링한다.

---

## 4. 의존성 주입과 단위 테스트

계층은 나뉘었지만 `BoardRepository`가 싱글턴 `object`여서 ViewModel이 직접 호출하고 있었다.
바꿔 끼울 수 없는 상태라 테스트에 실제 서버가 필요했다.

| 대상 | 이전 | 이후 |
|---|---|---|
| `BoardRepository` | 싱글턴 `object` | `interface` + `RemoteBoardRepository` |
| ViewModel의 의존성 | 싱글턴 직접 호출 | 생성자 주입 |
| ViewModel 생성 | `viewModel()` | `viewModelFactory` |
| 조립 위치 | 흩어짐 | `AppContainer` |

ViewModel 단위 테스트 15개를 추가했다. `FakeBoardRepository`로 성공·실패를 만들고 호출 횟수를 센다.

### 테스트가 잡아낸 결함

연타 방지가 코루틴 디스패처에 의존하고 있었다.

```kotlin
// 이전 — 코루틴 안에서 잠근다
if (isSubmitting) return
viewModelScope.launch { isSubmitting = true; ... }

// 이후 — 예약 전에 잠근다
if (isSubmitting) return
isSubmitting = true
viewModelScope.launch { ... }
```

코루틴이 즉시 시작되는 환경에서는 우연히 동작하지만, 시작이 지연되면 그 사이에 들어온 호출이
가드를 그대로 통과한다. 실기기 검증에서는 드러나지 않았고 단위 테스트에서 처음 잡혔다.

---

## 프로젝트 식별자 정리

생성 당시의 임시 이름과 오타가 남아 있었다.

| 항목 | 이전 | 이후 |
|---|---|---|
| `applicationId` · `namespace` | `com.start.appForStuding` | `com.bonukoo.board` |
| `rootProject.name` | `my_project` | `compose-board-app` |
| 앱 표시 이름 | `my_project` | `게시판` |
| 테마 | `Theme.My_project` · `My_projectTheme` | `Theme.Board` · `BoardTheme` |
| 최상위 Composable | `Neo4App` | `BoardApp` |

`applicationId`는 스토어에서 앱을 식별하는 값이라 출시 후에는 바꿀 수 없어 미출시 상태인 시점에 정리했다.

---

## 검증

각 단계마다 에뮬레이터에서 실제 서버와 연동해 확인했다.
요청 횟수는 서버 SQL 로그로 셌고, 오류 상태는 서버를 중지시켜 재현했다.
회전·재시도 복구·삭제 후 목록 갱신을 포함해 다섯 가지 흐름을 모두 거쳤다.
