package com.bonukoo.board.data

import com.bonukoo.board.domain.Board
import com.bonukoo.board.server.RetrofitBuilder
import com.bonukoo.board.server.request.RequestBoard

/**
 * 게시글 데이터에 접근하는 유일한 창구.
 *
 * 화면은 Retrofit 도, 통신용 모델인 RequestBoard 도 알 필요가 없다.
 * 서버 호출과 모델 변환을 이 한 곳에 모아 두면
 * 나중에 ViewModel 이 이 객체만 주입받아 쓸 수 있고, 테스트에서는 가짜 구현으로 바꿔 끼울 수 있다.
 */
object BoardRepository {

    private val boardService get() = RetrofitBuilder.getBoardService()

    suspend fun getBoards(): List<Board> =
        boardService.getBoardList().map { it.toDomain() }

    suspend fun getBoard(id: Int): Board =
        boardService.getBoardById(id).toDomain()

    suspend fun create(title: String, content: String, writer: String) {
        // id 는 서버가 채번하므로 요청에는 의미가 없다.
        // 이 사정을 화면이 알 필요는 없어 여기에서만 다룬다.
        boardService.createBoard(
            RequestBoard(id = 0, title = title, content = content, name = writer)
        )
    }

    suspend fun update(board: Board) {
        boardService.updateBoard(board.toRequest())
    }

    suspend fun delete(id: Int) {
        boardService.deleteBoard(id)
    }
}

private fun RequestBoard.toDomain() = Board(
    id = id,
    title = title,
    content = content,
    writer = name,
)

private fun Board.toRequest() = RequestBoard(
    id = id,
    title = title,
    content = content,
    name = writer,
)
