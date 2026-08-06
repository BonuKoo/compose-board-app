package com.bonukoo.board.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bonukoo.board.ui.Navigate
import com.bonukoo.board.ui.foundation.icon.BackIcon
import com.bonukoo.board.ui.theme.Gray
import com.bonukoo.board.ui.theme.Purple40
import com.bonukoo.board.ui.theme.White

@Composable
fun CreateBoardScreen(
    viewModel: CreateBoardViewModel = viewModel(factory = CreateBoardViewModel.Factory),
    onMove: (String) -> Unit
) {
    val context = LocalContext.current

    CreateBoardContent(
        title = viewModel.title,
        content = viewModel.content,
        writer = viewModel.writer,
        isSubmitting = viewModel.isSubmitting,
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        onWriterChange = viewModel::onWriterChange,
        onSubmit = {
            viewModel.submit(
                onCreated = { onMove(Navigate.BOARD_LIST.name) },
                onFailed = {
                    Toast.makeText(context, "게시글 등록 실패", Toast.LENGTH_SHORT).show()
                }
            )
        },
        onBack = { onMove(Navigate.BOARD_LIST.name) }
    )
}

@Composable
private fun CreateBoardContent(
    title: String,
    content: String,
    writer: String,
    isSubmitting: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onWriterChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
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
                    onClick = onBack
                ) {
                    BackIcon(descriptor = "뒤로가기")
                }
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = title,
                onValueChange = onTitleChange,
                label = { Text(text = "제목") },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = content,
                onValueChange = onContentChange,
                label = { Text(text = "내용") },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                value = writer,
                onValueChange = onWriterChange,
                label = { Text(text = "작성자") },
                singleLine = true
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 30.dp, vertical = 40.dp),
            // 전송 중에는 눌리지 않는다. 연타로 게시글이 여러 개 만들어지던 문제를 막는다.
            enabled = !isSubmitting,
            onClick = onSubmit,
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
                text = if (isSubmitting) "등록 중..." else "게시글 등록",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}

@Composable
@Preview(showBackground = true, name = "작성")
private fun CreateBoardPreview() {
    CreateBoardContent(
        title = "제목입니다", content = "내용입니다", writer = "작성자",
        isSubmitting = false,
        onTitleChange = {}, onContentChange = {}, onWriterChange = {},
        onSubmit = {}, onBack = {}
    )
}

@Composable
@Preview(showBackground = true, name = "등록 중")
private fun CreateBoardSubmittingPreview() {
    CreateBoardContent(
        title = "제목입니다", content = "내용입니다", writer = "작성자",
        isSubmitting = true,
        onTitleChange = {}, onContentChange = {}, onWriterChange = {},
        onSubmit = {}, onBack = {}
    )
}
