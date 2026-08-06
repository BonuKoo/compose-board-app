package com.bonukoo.board.ui.screens

import com.bonukoo.board.MainDispatcherRule
import com.bonukoo.board.data.FakeBoardRepository
import com.bonukoo.board.domain.Board
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateBoardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val existing = Board(1, "첫 게시글", "내용 A", "홍길동")

    @Test
    fun `조회에 성공하면 기존 값이 입력란에 채워진다`() = runTest {
        val viewModel = UpdateBoardViewModel(FakeBoardRepository(listOf(existing)))

        viewModel.load(1)
        advanceUntilIdle()

        assertEquals("첫 게시글", viewModel.title)
        assertEquals("내용 A", viewModel.content)
        assertTrue(viewModel.canSubmit)
    }

    @Test
    fun `조회에 실패하면 저장할 수 없다`() = runTest {
        val repository = FakeBoardRepository(listOf(existing)).apply { shouldFail = true }
        val viewModel = UpdateBoardViewModel(repository)

        viewModel.load(1)
        advanceUntilIdle()

        assertTrue(viewModel.loadFailed)
        assertFalse(viewModel.canSubmit)
    }

    @Test
    fun `조회에 실패한 상태에서 저장을 눌러도 요청이 나가지 않는다`() = runTest {
        val repository = FakeBoardRepository(listOf(existing)).apply { shouldFail = true }
        val viewModel = UpdateBoardViewModel(repository)

        viewModel.load(1)
        advanceUntilIdle()

        repository.shouldFail = false
        viewModel.submit(onUpdated = {}, onFailed = {})
        advanceUntilIdle()

        // 게시글을 확보하지 못했으므로 id 가 없는 요청을 보내지 않아야 한다.
        assertEquals(0, repository.updateCallCount)
    }

    @Test
    fun `제목과 내용만 바뀌고 작성자와 id 는 유지된다`() = runTest {
        val repository = FakeBoardRepository(listOf(existing))
        val viewModel = UpdateBoardViewModel(repository)

        viewModel.load(1)
        advanceUntilIdle()

        viewModel.onTitleChange("고친 제목")
        viewModel.onContentChange("고친 내용")
        viewModel.submit(onUpdated = {}, onFailed = {})
        advanceUntilIdle()

        val sent = repository.lastUpdated
        assertEquals(Board(1, "고친 제목", "고친 내용", "홍길동"), sent)
    }

    @Test
    fun `이미 불러온 뒤에는 다시 불러와도 고치던 내용을 덮어쓰지 않는다`() = runTest {
        val viewModel = UpdateBoardViewModel(FakeBoardRepository(listOf(existing)))

        viewModel.load(1)
        advanceUntilIdle()
        viewModel.onTitleChange("고치는 중")

        // 화면 회전 등으로 load 가 다시 불릴 수 있다.
        viewModel.load(1)
        advanceUntilIdle()

        assertEquals("고치는 중", viewModel.title)
    }
}
