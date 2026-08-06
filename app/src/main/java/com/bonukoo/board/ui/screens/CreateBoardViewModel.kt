package com.bonukoo.board.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bonukoo.board.data.BoardRepository
import com.bonukoo.board.di.AppContainer
import kotlinx.coroutines.launch

/**
 * 작성 화면의 입력값과 전송 상태를 맡는다.
 *
 * 입력값을 ViewModel 이 들고 있으므로 화면을 회전해도 쓰던 글이 사라지지 않는다.
 * isSubmitting 으로 전송 중 재요청을 막는다. 이전에는 등록 버튼을 연타하면
 * 누른 횟수만큼 게시글이 만들어졌다.
 */
class CreateBoardViewModel(
    private val repository: BoardRepository
) : ViewModel() {

    var title by mutableStateOf("")
        private set
    var content by mutableStateOf("")
        private set
    var writer by mutableStateOf("")
        private set
    var isSubmitting by mutableStateOf(false)
        private set

    fun onTitleChange(value: String) { title = value }
    fun onContentChange(value: String) { content = value }
    fun onWriterChange(value: String) { writer = value }

    fun submit(onCreated: () -> Unit, onFailed: () -> Unit) {
        if (isSubmitting) return
        // 코루틴 안에서 켜면 코루틴이 실제로 시작되기 전까지 가드가 열려 있어
        // 연타가 그대로 통과한다. 실행을 예약하기 전에 잠근다.
        isSubmitting = true

        viewModelScope.launch {
            runCatching {
                repository.create(title = title, content = content, writer = writer)
            }.onSuccess {
                onCreated()
            }.onFailure { error ->
                isSubmitting = false
                error.printStackTrace()
                onFailed()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { CreateBoardViewModel(AppContainer.boardRepository) }
        }
    }
}
