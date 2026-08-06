package com.bonukoo.board.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonukoo.board.data.BoardRepository
import com.bonukoo.board.domain.Board
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BoardDetailUiState {
    data object Loading : BoardDetailUiState
    data class Success(val board: Board) : BoardDetailUiState
    data class Error(val message: String) : BoardDetailUiState
}

/**
 * 상세 화면의 상태와 삭제 동작을 맡는다.
 *
 * 코루틴을 viewModelScope 에서 실행하므로 화면을 벗어나면 자동으로 취소된다.
 * 이전에는 MainScope() 를 직접 만들어 써서 화면을 떠나도 코루틴이 남았다.
 */
class BoardDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<BoardDetailUiState>(BoardDetailUiState.Loading)
    val uiState: StateFlow<BoardDetailUiState> = _uiState.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            if (_uiState.value !is BoardDetailUiState.Success) {
                _uiState.value = BoardDetailUiState.Loading
            }

            runCatching {
                BoardRepository.getBoard(id)
            }.onSuccess { board ->
                _uiState.value = BoardDetailUiState.Success(board)
            }.onFailure { error ->
                _uiState.value = BoardDetailUiState.Error("게시글을 불러오지 못했습니다.")
                error.printStackTrace()
            }
        }
    }

    fun delete(id: Int, onDeleted: () -> Unit, onFailed: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                BoardRepository.delete(id)
            }.onSuccess {
                onDeleted()
            }.onFailure { error ->
                error.printStackTrace()
                onFailed()
            }
        }
    }
}
