package com.start.appForStuding.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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

import com.start.appForStuding.data.BoardRepository
import com.start.appForStuding.domain.Board
import com.start.appForStuding.ui.Navigate
import com.start.appForStuding.ui.foundation.icon.BackIcon
import com.start.appForStuding.ui.theme.Gray
import com.start.appForStuding.ui.theme.Purple40
import com.start.appForStuding.ui.theme.White

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@Composable
fun UpdateBoardScreen(
    id: Int,
    onMove: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var board by remember { mutableStateOf(Board(0, "", "", "")) }
    val context = LocalContext.current

    // LaunchedEffect : 코루틴을 실행하고 화면을 벗어나면 자동으로 취소한다.
    // DisposableEffect 는 직접 정리해야 하는 리소스를 다룰 때 쓴다.
    LaunchedEffect(id) {
        runCatching {
            BoardRepository.getBoard(id)
        }.onSuccess { result ->
            board = result
            title = result.title
            content = result.content
        }.onFailure { error ->
            board = Board(0, "값을 못 불러왔습니다.", "", "")
            error.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(White)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "게시글 수정",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = {
                        onMove(Navigate.READ.name)
                    }
                ) {
                    BackIcon(descriptor = "뒤로가기")
                }
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = title,
                onValueChange = { title = it },
                label = { Text(text = "제목") },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = content,
                onValueChange = { content = it },
                label = { Text(text = "내용") },
                singleLine = true
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 30.dp, vertical = 40.dp),
            onClick = {
                // 조회해 온 게시글에서 제목과 내용만 바꿔 보낸다.
                val updatedBoard = board.copy(title = title, content = content)
                MainScope().launch {
                    runCatching {
                        BoardRepository.update(updatedBoard)
                    }.onSuccess { _ ->
                        onMove(Navigate.READ.name)
                    }.onFailure { error ->
                        Toast.makeText(context, "게시글 갱신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        error.printStackTrace()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple40,
                contentColor = White,
                disabledContainerColor = Gray,
                disabledContentColor = White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                modifier = Modifier,
                text = "게시글 갱신",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun UpdateBoardScreenPreview() {
    UpdateBoardScreen(id = 1) { }
}