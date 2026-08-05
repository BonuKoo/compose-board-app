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
import com.start.appForStuding.server.RetrofitBuilder.getBoardService
import com.start.appForStuding.server.request.RequestBoard
import com.start.appForStuding.ui.Navigate
import com.start.appForStuding.ui.foundation.icon.BackIcon
import com.start.appForStuding.ui.theme.Gray
import com.start.appForStuding.ui.theme.Purple40
import com.start.appForStuding.ui.theme.White
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun CreateBoardScreen(onMove: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var writer by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                    text = "게시글 생성",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = {
                        onMove(Navigate.BOARD_LIST.name)
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

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = writer,
                onValueChange = { writer = it },
                label = { Text(text = "작성자") },
                singleLine = true
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 30.dp, vertical = 40.dp),
            onClick = {
                // MainScope -> 비동기 코드를 쓸 수 있는 화면 가능
                MainScope().launch {
                    val requestBody = RequestBoard(
                        id = 0,
                        title = title,
                        content = content,
                        name = writer
                    )
                    // 성공했을 때, 실패했을 때를 try-catch처럼 구성
                    runCatching {
                        getBoardService().createBoard(requestBody)
                    }.onSuccess {
                        onMove(Navigate.BOARD_LIST.name)
                    }.onFailure {
                        //Toast : 알림창이라고 생각하면 편하다.
                        Toast.makeText(context, "게시글 등록 실패", Toast.LENGTH_SHORT).show()
                        it.printStackTrace()
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
                text = "게시글 등록",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CreateBoardScreenPreview() {
    CreateBoardScreen() {

    }
}