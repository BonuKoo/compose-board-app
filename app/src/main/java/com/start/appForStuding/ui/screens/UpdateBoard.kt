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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.start.appForStuding.ui.Navigate
import com.start.appForStuding.ui.foundation.icon.BackIcon
import com.start.appForStuding.ui.theme.Gray
import com.start.appForStuding.ui.theme.Purple40
import com.start.appForStuding.ui.theme.White

@Composable
fun UpdateBoardScreen(
    id: Int,
    viewModel: UpdateBoardViewModel = viewModel(),
    onMove: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(id) {
        viewModel.load(id)
    }

    UpdateBoardContent(
        title = viewModel.title,
        content = viewModel.content,
        canSubmit = viewModel.canSubmit,
        isSubmitting = viewModel.isSubmitting,
        loadFailed = viewModel.loadFailed,
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        onSubmit = {
            viewModel.submit(
                onUpdated = { onMove(Navigate.READ.name) },
                onFailed = {
                    Toast.makeText(context, "게시글 갱신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        },
        onBack = { onMove(Navigate.READ.name) }
    )
}

@Composable
private fun UpdateBoardContent(
    title: String,
    content: String,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    loadFailed: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
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
                    text = "게시글 수정",
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

            if (loadFailed) {
                Text(
                    modifier = Modifier.padding(horizontal = 30.dp),
                    text = "게시글을 불러오지 못했습니다.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray
                )
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
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 30.dp, vertical = 40.dp),
            // 조회에 성공해야만 저장할 수 있다.
            // 이전에는 조회 실패 시 id 가 0 인 채로 전송되어 서버가 500 을 반환했다.
            enabled = canSubmit,
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
                text = if (isSubmitting) "갱신 중..." else "게시글 갱신",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}

@Composable
@Preview(showBackground = true, name = "수정")
private fun UpdateBoardPreview() {
    UpdateBoardContent(
        title = "첫 게시글", content = "내용 A",
        canSubmit = true, isSubmitting = false, loadFailed = false,
        onTitleChange = {}, onContentChange = {}, onSubmit = {}, onBack = {}
    )
}

@Composable
@Preview(showBackground = true, name = "조회 실패")
private fun UpdateBoardLoadFailedPreview() {
    UpdateBoardContent(
        title = "", content = "",
        canSubmit = false, isSubmitting = false, loadFailed = true,
        onTitleChange = {}, onContentChange = {}, onSubmit = {}, onBack = {}
    )
}
