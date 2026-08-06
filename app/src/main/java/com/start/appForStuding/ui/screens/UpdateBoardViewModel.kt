package com.start.appForStuding.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.start.appForStuding.data.BoardRepository
import com.start.appForStuding.domain.Board
import kotlinx.coroutines.launch

/**
 * 수정 화면의 입력값과 전송 상태를 맡는다.
 *
 * 조회해 온 게시글을 그대로 보관했다가 제목과 내용만 바꿔 보낸다.
 * 조회에 실패하면 loadFailed 가 되어 저장 버튼을 막는다.
 * 이전에는 조회 실패 시 id 가 0 인 채로 전송되어 서버가 500 을 반환했다.
 */
class UpdateBoardViewModel : ViewModel() {

    private var board: Board? = null

    var title by mutableStateOf("")
        private set
    var content by mutableStateOf("")
        private set
    var isSubmitting by mutableStateOf(false)
        private set
    var loadFailed by mutableStateOf(false)
        private set

    val canSubmit: Boolean get() = board != null && !isSubmitting

    fun onTitleChange(value: String) { title = value }
    fun onContentChange(value: String) { content = value }

    fun load(id: Int) {
        // 회전 등으로 다시 호출되어도 이미 불러온 값과 사용자가 고친 내용을 지우지 않는다.
        if (board != null) return

        viewModelScope.launch {
            runCatching {
                BoardRepository.getBoard(id)
            }.onSuccess { loaded ->
                board = loaded
                title = loaded.title
                content = loaded.content
                loadFailed = false
            }.onFailure { error ->
                loadFailed = true
                error.printStackTrace()
            }
        }
    }

    fun submit(onUpdated: () -> Unit, onFailed: () -> Unit) {
        val current = board ?: return
        if (isSubmitting) return

        viewModelScope.launch {
            isSubmitting = true
            runCatching {
                BoardRepository.update(current.copy(title = title, content = content))
            }.onSuccess {
                onUpdated()
            }.onFailure { error ->
                isSubmitting = false
                error.printStackTrace()
                onFailed()
            }
        }
    }
}
