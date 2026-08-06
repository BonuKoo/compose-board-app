package com.start.appForStuding.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.start.appForStuding.domain.Board as BoardItem
import com.start.appForStuding.ui.Navigate
import com.start.appForStuding.ui.component.Board
import com.start.appForStuding.ui.foundation.icon.AddIcon
import com.start.appForStuding.ui.theme.Background
import com.start.appForStuding.ui.theme.Black
import com.start.appForStuding.ui.theme.Gray
import com.start.appForStuding.ui.theme.Purple40
import com.start.appForStuding.ui.theme.White

/**
 * 상태를 ViewModel 에서 받아 화면에 연결한다.
 * 실제로 그리는 일은 BoardListContent 가 맡는다.
 */
@Composable
fun BoardListScreen(
    viewModel: BoardListViewModel = viewModel(),
    onMove: (String, id: Int?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 화면에 들어올 때마다 다시 불러온다.
    // 글을 쓰거나 지우고 돌아왔을 때 최신 목록이 보이게 한다.
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    BoardListContent(
        uiState = uiState,
        onRetry = viewModel::load,
        onMove = onMove
    )
}

/**
 * 상태만 받아서 그리는 화면. ViewModel 을 모르므로 Preview 에서도 그대로 쓸 수 있다.
 */
@Composable
private fun BoardListContent(
    uiState: BoardListUiState,
    onRetry: () -> Unit,
    onMove: (String, id: Int?) -> Unit
) {
    Box(
        modifier = Modifier
            .background(Background)
            .fillMaxSize()
    ) {
        Column {
            // topbar
            Box(
                modifier = Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier,
                    text = "Neo4의 게시판",
                    color = Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.padding(top = 5.dp))

            when (uiState) {
                is BoardListUiState.Loading -> CenterMessage {
                    CircularProgressIndicator(color = Purple40)
                }

                is BoardListUiState.Error -> CenterMessage {
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

                is BoardListUiState.Success ->
                    if (uiState.boards.isEmpty()) {
                        CenterMessage {
                            Text(
                                text = "아직 게시글이 없습니다.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            items(uiState.boards.size) { index ->
                                val data = uiState.boards[index]
                                Board(
                                    title = data.title,
                                    writer = data.writer
                                ) {
                                    onMove(Navigate.READ.name, data.id)
                                }
                            }
                        }
                    }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    vertical = 40.dp,
                    horizontal = 30.dp,
                ),
            shape = CircleShape,
            containerColor = Purple40,
            onClick = { onMove(Navigate.CREATE.name, null) }
        ) {
            AddIcon(
                modifier = Modifier
                    .padding(5.dp),
                descriptor = "add", tint = White
            )
        }
    }
}

/** 목록 대신 보여 줄 안내를 화면 가운데에 배치한다. */
@Composable
private fun CenterMessage(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            content()
        }
    }
}

@Composable
@Preview(showBackground = true, name = "목록")
private fun BoardListSuccessPreview() {
    BoardListContent(
        uiState = BoardListUiState.Success(
            listOf(
                BoardItem(1, "첫 게시글", "내용 A", "홍길동"),
                BoardItem(2, "두 번째 글", "내용 B", "김철수"),
            )
        ),
        onRetry = {},
        onMove = { _, _ -> }
    )
}

@Composable
@Preview(showBackground = true, name = "불러오는 중")
private fun BoardListLoadingPreview() {
    BoardListContent(uiState = BoardListUiState.Loading, onRetry = {}, onMove = { _, _ -> })
}

@Composable
@Preview(showBackground = true, name = "비어 있음")
private fun BoardListEmptyPreview() {
    BoardListContent(
        uiState = BoardListUiState.Success(emptyList()),
        onRetry = {},
        onMove = { _, _ -> }
    )
}

@Composable
@Preview(showBackground = true, name = "오류")
private fun BoardListErrorPreview() {
    BoardListContent(
        uiState = BoardListUiState.Error("게시글을 불러오지 못했습니다."),
        onRetry = {},
        onMove = { _, _ -> }
    )
}
