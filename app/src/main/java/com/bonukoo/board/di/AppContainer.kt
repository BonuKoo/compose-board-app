package com.bonukoo.board.di

import com.bonukoo.board.data.BoardRepository
import com.bonukoo.board.data.RemoteBoardRepository

/**
 * 앱이 실제로 사용할 구현을 조립하는 곳.
 *
 * 의존성을 만드는 일을 한 곳에 모아 두면, 나머지 코드는 인터페이스만 알면 된다.
 * ViewModel 은 BoardRepository 를 생성자로 받으므로 테스트에서는
 * 이 컨테이너를 거치지 않고 가짜 구현을 직접 넘길 수 있다.
 */
object AppContainer {
    val boardRepository: BoardRepository by lazy { RemoteBoardRepository() }
}
