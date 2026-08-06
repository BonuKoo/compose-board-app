package com.bonukoo.board.domain

/**
 * 화면에서 사용하는 게시글 모델.
 *
 * 통신 규격인 RequestBoard 와 분리해 두었다.
 * 서버 응답의 필드 이름이나 구조가 바뀌어도 변환은 BoardRepository 가 흡수하므로
 * 화면 코드는 영향을 받지 않는다.
 */
data class Board(
    val id: Int,
    val title: String,
    val content: String,
    val writer: String,
)
