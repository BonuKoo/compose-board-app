package com.bonukoo.board.data

import com.bonukoo.board.domain.Board
import com.bonukoo.board.server.RetrofitBuilder
import com.bonukoo.board.server.request.RequestBoard
import com.bonukoo.board.server.service.BoardService

/**
 * REST API 서버를 통해 게시글을 다루는 구현.
 *
 * 통신용 모델(RequestBoard)과 화면용 모델(Board) 사이의 변환을 여기에서 맡는다.
 * 서버 응답 형식이 바뀌어도 이 파일만 고치면 된다.
 */
class RemoteBoardRepository(
    private val boardService: BoardService = RetrofitBuilder.getBoardService()
) : BoardRepository {

    override suspend fun getBoards(): List<Board> =
        boardService.getBoardList().map { it.toDomain() }

    override suspend fun getBoard(id: Int): Board =
        boardService.getBoardById(id).toDomain()

    override suspend fun create(title: String, content: String, writer: String) {
        // id 는 서버가 채번하므로 요청에는 의미가 없다.
        // 이 사정을 화면이 알 필요는 없어 여기에서만 다룬다.
        boardService.createBoard(
            RequestBoard(id = 0, title = title, content = content, name = writer)
        )
    }

    override suspend fun update(board: Board) {
        boardService.updateBoard(board.toRequest())
    }

    override suspend fun delete(id: Int) {
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
