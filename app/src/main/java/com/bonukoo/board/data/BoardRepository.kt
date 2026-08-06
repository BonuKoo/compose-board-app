package com.bonukoo.board.data

import com.bonukoo.board.domain.Board

/**
 * 게시글 데이터에 접근하는 창구.
 *
 * ViewModel 은 이 인터페이스만 알기 때문에 실제 구현이 무엇인지 신경 쓰지 않는다.
 * 앱은 서버를 호출하는 RemoteBoardRepository 를 쓰고,
 * 테스트는 서버 없이 동작하는 가짜 구현을 넣는다.
 */
interface BoardRepository {
    suspend fun getBoards(): List<Board>
    suspend fun getBoard(id: Int): Board
    suspend fun create(title: String, content: String, writer: String)
    suspend fun update(board: Board)
    suspend fun delete(id: Int)
}
