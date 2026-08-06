package com.start.appForStuding.ui.screens


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.start.appForStuding.ui.theme.Background
import com.start.appForStuding.ui.theme.White

import com.start.appForStuding.domain.Board
import com.start.appForStuding.ui.Navigate
import com.start.appForStuding.ui.foundation.icon.BackIcon
import com.start.appForStuding.ui.foundation.icon.MoreIcon
import com.start.appForStuding.ui.theme.Black
import com.start.appForStuding.ui.theme.Gray
import com.start.appForStuding.ui.theme.Purple40

@Composable
fun ShowBoardScreen(
    id: Int,
    viewModel: BoardDetailViewModel = viewModel(),
    onMove: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(id) {
        viewModel.load(id)
    }

    ShowBoardContent(
        uiState = uiState,
        onRetry = { viewModel.load(id) },
        onDelete = {
            viewModel.delete(
                id = id,
                onDeleted = { onMove(Navigate.BOARD_LIST.name) },
                onFailed = {
                    Toast.makeText(context, "게시글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        },
        onMove = onMove
    )
}

@Composable
private fun ShowBoardContent(
    uiState: BoardDetailUiState,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
    onMove: (String) -> Unit
) {
    var popupMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(Background)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .background(White)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onMove(Navigate.BOARD_LIST.name) }
            ) {
                BackIcon(descriptor = "뒤로가기")
            }
            // 게시글을 불러온 뒤에만 수정·삭제를 노출한다.
            if (uiState is BoardDetailUiState.Success) {
                // DropdownMenu 는 IconButton 의 content 슬롯이 아니라 형제로 두어야
                // 48dp 고정 크기 컨테이너에 갇히지 않고 올바른 위치에 열린다.
                Box {
                    IconButton(
                        onClick = { popupMenuExpanded = true }
                    ) {
                        MoreIcon(descriptor = "더보기")
                    }
                    DropdownMenu(
                        expanded = popupMenuExpanded,
                        onDismissRequest = { popupMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "수정하기",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Black
                                )
                            },
                            onClick = {
                                popupMenuExpanded = false
                                onMove(Navigate.UPDATE.name)
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "삭제하기",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Black
                                )
                            },
                            onClick = {
                                popupMenuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 3.dp))

        when (uiState) {
            is BoardDetailUiState.Loading -> CenterBox {
                CircularProgressIndicator(color = Purple40)
            }

            is BoardDetailUiState.Error -> CenterBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.message,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray
                    )
                    TextButton(onClick = onRetry) {
                        Text(
                            text = "다시 시도",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple40
                        )
                    }
                }
            }

            is BoardDetailUiState.Success -> BoardBody(board = uiState.board)
        }
    }
}

@Composable
private fun BoardBody(board: Board) {
    Column(
        modifier = Modifier
            .background(White)
            .padding(horizontal = 30.dp, vertical = 20.dp)
    ) {
        Text(
            text = board.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )
        Spacer(modifier = Modifier.padding(top = 5.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = board.content,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Black
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        if (board.writer.isNotBlank()) {
            Row {
                Text(
                    text = "작성자:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                Spacer(modifier = Modifier.padding(start = 5.dp))
                Text(
                    text = board.writer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray
                )
            }
        }
    }
}

@Composable
private fun CenterBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
@Preview(showBackground = true, name = "게시글")
private fun ShowBoardSuccessPreview() {
    ShowBoardContent(
        uiState = BoardDetailUiState.Success(
            Board(1, "첫 게시글", "내용 A", "홍길동")
        ),
        onRetry = {}, onDelete = {}, onMove = {}
    )
}

@Composable
@Preview(showBackground = true, name = "불러오는 중")
private fun ShowBoardLoadingPreview() {
    ShowBoardContent(
        uiState = BoardDetailUiState.Loading,
        onRetry = {}, onDelete = {}, onMove = {}
    )
}

@Composable
@Preview(showBackground = true, name = "오류")
private fun ShowBoardErrorPreview() {
    ShowBoardContent(
        uiState = BoardDetailUiState.Error("게시글을 불러오지 못했습니다."),
        onRetry = {}, onDelete = {}, onMove = {}
    )
}
