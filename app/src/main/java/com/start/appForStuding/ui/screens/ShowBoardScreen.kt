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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.start.appForStuding.ui.theme.Background
import com.start.appForStuding.ui.theme.White

import com.start.appForStuding.server.RetrofitBuilder.getBoardService
import com.start.appForStuding.server.request.RequestBoard
import com.start.appForStuding.ui.Navigate
import com.start.appForStuding.ui.foundation.icon.BackIcon
import com.start.appForStuding.ui.foundation.icon.MoreIcon
import com.start.appForStuding.ui.theme.Black
import com.start.appForStuding.ui.theme.Gray

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun ShowBoardScreen(
    id: Int,
    onMove: (String) -> Unit
) {
    var board by remember { mutableStateOf<RequestBoard>(RequestBoard(0, "", "", "")) }
    var popupMenuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // id 가 바뀌면 다시 조회한다. SideEffect 를 쓰면 메뉴를 열고 닫을 때마다 요청이 나간다.
    LaunchedEffect(id) {
        runCatching {
            getBoardService().getBoardById(id = id)
        }.onSuccess { result ->
            board = result
        }.onFailure { error ->
            board = RequestBoard(0, "값을 못 불러왔습니다.", "", "")
            error.printStackTrace()
        }
    }

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
            // DropdownMenu 는 IconButton 의 content 슬롯이 아니라 형제로 두어야
            // 48dp 고정 크기 컨테이너에 갇히지 않고 올바른 위치에 열린다.
            Box {
                IconButton(
                    onClick = {
                        popupMenuExpanded = true
                    }
                ) {
                    MoreIcon(descriptor = "더보기")
                }
                DropdownMenu(
                    expanded = popupMenuExpanded,
                    onDismissRequest = {
                        popupMenuExpanded = false
                    }
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
                            MainScope().launch {
                                runCatching {
                                    getBoardService().deleteBoard(id)
                                }.onSuccess {
                                    onMove(Navigate.BOARD_LIST.name)
                                }.onFailure { error ->
                                    Toast.makeText(context, "게시글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                    error.printStackTrace()
                                }
                            }
                            popupMenuExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 3.dp))
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
            if (board.name.isNotBlank()) {
                Row {
                    Text(
                        text = "작성자:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                    Spacer(modifier = Modifier.padding(start = 5.dp))
                    Text(
                        text = board.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ShowBoardPreview() {
    ShowBoardScreen(id = 1) {

    }
}