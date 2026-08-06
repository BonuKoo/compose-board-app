package com.bonukoo.board.ui.screens

import com.bonukoo.board.MainDispatcherRule
import com.bonukoo.board.data.FakeBoardRepository
import com.bonukoo.board.domain.Board
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sampleBoards = listOf(
        Board(1, "첫 게시글", "내용 A", "홍길동"),
        Board(2, "두 번째 글", "내용 B", "김철수"),
    )

    @Test
    fun `처음에는 로딩 상태다`() {
        val viewModel = BoardListViewModel(FakeBoardRepository(sampleBoards))

        assertEquals(BoardListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `조회에 성공하면 목록을 담은 성공 상태가 된다`() = runTest {
        val viewModel = BoardListViewModel(FakeBoardRepository(sampleBoards))

        viewModel.load()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BoardListUiState.Success)
        assertEquals(sampleBoards, (state as BoardListUiState.Success).boards)
    }

    @Test
    fun `게시글이 없으면 빈 목록을 담은 성공 상태가 된다`() = runTest {
        val viewModel = BoardListViewModel(FakeBoardRepository(emptyList()))

        viewModel.load()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BoardListUiState.Success)
        assertTrue((state as BoardListUiState.Success).boards.isEmpty())
    }

    @Test
    fun `조회에 실패하면 오류 상태가 된다`() = runTest {
        val repository = FakeBoardRepository(sampleBoards).apply { shouldFail = true }
        val viewModel = BoardListViewModel(repository)

        viewModel.load()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is BoardListUiState.Error)
    }

    @Test
    fun `이미 목록을 보여 주는 중이면 다시 불러올 때 로딩 화면으로 되돌아가지 않는다`() = runTest {
        val repository = FakeBoardRepository(sampleBoards)
        val viewModel = BoardListViewModel(repository)

        viewModel.load()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is BoardListUiState.Success)

        // 두 번째 호출 직후에도 성공 상태를 유지해야 화면이 깜빡이지 않는다.
        viewModel.load()
        assertTrue(viewModel.uiState.value is BoardListUiState.Success)
    }

    @Test
    fun `실패한 뒤 다시 시도해서 성공하면 목록이 복구된다`() = runTest {
        val repository = FakeBoardRepository(sampleBoards).apply { shouldFail = true }
        val viewModel = BoardListViewModel(repository)

        viewModel.load()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is BoardListUiState.Error)

        repository.shouldFail = false
        viewModel.load()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is BoardListUiState.Success)
    }
}
