package com.start.appForStuding.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.start.appForStuding.data.BoardRepository
import com.start.appForStuding.domain.Board
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 목록 화면이 가질 수 있는 상태.
 *
 * 이전에는 성공한 데이터만 remember 로 들고 있어서
 * "불러오는 중"과 "글이 하나도 없음"과 "실패"가 모두 빈 화면으로 보였다.
 */
sealed interface BoardListUiState {
    data object Loading : BoardListUiState
    data class Success(val boards: List<Board>) : BoardListUiState
    data class Error(val message: String) : BoardListUiState
}

/**
 * 목록 화면의 상태를 보관한다.
 *
 * ViewModel 은 화면 회전으로 액티비티가 다시 만들어져도 살아남는다.
 * 덕분에 회전 시 데이터가 사라지지 않고, 화면은 그리기에만 집중할 수 있다.
 */
class BoardListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<BoardListUiState>(BoardListUiState.Loading)
    val uiState: StateFlow<BoardListUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            // 이미 보여 줄 데이터가 있으면 로딩 화면으로 되돌리지 않는다.
            // 회전하거나 목록으로 되돌아올 때 화면이 깜빡이는 것을 막는다.
            if (_uiState.value !is BoardListUiState.Success) {
                _uiState.value = BoardListUiState.Loading
            }

            runCatching {
                BoardRepository.getBoards()
            }.onSuccess { boards ->
                _uiState.value = BoardListUiState.Success(boards)
            }.onFailure { error ->
                _uiState.value = BoardListUiState.Error("게시글을 불러오지 못했습니다.")
                error.printStackTrace()
            }
        }
    }
}
