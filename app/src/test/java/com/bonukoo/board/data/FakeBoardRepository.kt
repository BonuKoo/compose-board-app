package com.bonukoo.board.data

import com.bonukoo.board.domain.Board

/**
 * 테스트용 게시글 저장소. 서버 없이 메모리에서만 동작한다.
 *
 * BoardRepository 가 인터페이스이기 때문에 ViewModel 에 이 구현을 그대로 넣을 수 있다.
 * shouldFail 을 켜면 모든 호출이 실패해 오류 처리 경로를 확인할 수 있고,
 * createCallCount 로 실제 호출 횟수를 셀 수 있다.
 */
class FakeBoardRepository(
    initialBoards: List<Board> = emptyList()
) : BoardRepository {

    private val boards = initialBoards.toMutableList()

    var shouldFail: Boolean = false
    var createCallCount: Int = 0
        private set
    var updateCallCount: Int = 0
        private set
    var deleteCallCount: Int = 0
        private set
    var lastUpdated: Board? = null
        private set

    private fun failIfNeeded() {
        if (shouldFail) throw RuntimeException("의도적으로 실패시킨 요청")
    }

    override suspend fun getBoards(): List<Board> {
        failIfNeeded()
        return boards.toList()
    }

    override suspend fun getBoard(id: Int): Board {
        failIfNeeded()
        return boards.first { it.id == id }
    }

    override suspend fun create(title: String, content: String, writer: String) {
        failIfNeeded()
        createCallCount++
        boards += Board(id = boards.size + 1, title = title, content = content, writer = writer)
    }

    override suspend fun update(board: Board) {
        failIfNeeded()
        updateCallCount++
        lastUpdated = board
    }

    override suspend fun delete(id: Int) {
        failIfNeeded()
        deleteCallCount++
        boards.removeAll { it.id == id }
    }
}
