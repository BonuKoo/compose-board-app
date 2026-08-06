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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.start.appForStuding.data.BoardRepository
import com.start.appForStuding.domain.Board as BoardItem
import com.start.appForStuding.ui.Navigate
import com.start.appForStuding.ui.component.Board
import com.start.appForStuding.ui.foundation.icon.AddIcon
import com.start.appForStuding.ui.theme.Background
import com.start.appForStuding.ui.theme.Black
import com.start.appForStuding.ui.theme.Purple40
import com.start.appForStuding.ui.theme.White


@Composable
fun BoardListScreen(onMove: (String, id: Int?) -> Unit) {
    var boardList by remember { mutableStateOf(listOf<BoardItem>()) }

    // LaunchedEffect : 화면에 진입할 때 한 번만 실행되고, 화면을 벗어나면 자동으로 취소된다.
    // SideEffect 는 재구성마다 실행되어 불필요한 요청이 반복된다.
    LaunchedEffect(Unit) {
        runCatching {
            BoardRepository.getBoards()
        }.onSuccess { result ->
            boardList = result
        }.onFailure { error ->
            error.printStackTrace()
        }
    }

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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                items(boardList.size) { index ->
                    val data = boardList[index]
                    Board(
                        title = data.title,
                        writer = data.writer
                    ) {
                        onMove(Navigate.READ.name, data.id)
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

@Composable
@Preview(showBackground = true)
fun BoardListScreenPreview() {
    BoardListScreen() { _, _ -> }
}