package com.bonukoo.board.ui.screens

import com.bonukoo.board.MainDispatcherRule
import com.bonukoo.board.data.FakeBoardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBoardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `입력값이 그대로 반영된다`() {
        val viewModel = CreateBoardViewModel(FakeBoardRepository())

        viewModel.onTitleChange("제목")
        viewModel.onContentChange("내용")
        viewModel.onWriterChange("작성자")

        assertEquals("제목", viewModel.title)
        assertEquals("내용", viewModel.content)
        assertEquals("작성자", viewModel.writer)
    }

    @Test
    fun `등록에 성공하면 완료 콜백이 호출된다`() = runTest {
        val repository = FakeBoardRepository()
        val viewModel = CreateBoardViewModel(repository)
        var created = false

        viewModel.onTitleChange("제목")
        viewModel.submit(onCreated = { created = true }, onFailed = {})
        advanceUntilIdle()

        assertTrue(created)
        assertEquals(1, repository.createCallCount)
    }

    @Test
    fun `등록 버튼을 연타해도 요청은 한 번만 나간다`() = runTest {
        val repository = FakeBoardRepository()
        val viewModel = CreateBoardViewModel(repository)

        // 첫 요청이 끝나기 전에 세 번 더 누른 상황
        repeat(4) {
            viewModel.submit(onCreated = {}, onFailed = {})
        }
        advanceUntilIdle()

        assertEquals(1, repository.createCallCount)
    }

    @Test
    fun `등록에 실패하면 실패 콜백이 호출되고 다시 시도할 수 있다`() = runTest {
        val repository = FakeBoardRepository().apply { shouldFail = true }
        val viewModel = CreateBoardViewModel(repository)
        var failed = false

        viewModel.submit(onCreated = {}, onFailed = { failed = true })
        advanceUntilIdle()

        assertTrue(failed)
        // 실패했으므로 전송 중 상태가 풀려 다시 누를 수 있어야 한다.
        assertFalse(viewModel.isSubmitting)

        repository.shouldFail = false
        viewModel.submit(onCreated = {}, onFailed = {})
        advanceUntilIdle()

        assertEquals(1, repository.createCallCount)
    }
}
