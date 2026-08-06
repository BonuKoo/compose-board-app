package com.bonukoo.board.ui.foundation.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bonukoo.board.R
import kotlinx.serialization.descriptors.SerialDescriptor



@Composable
fun AddIcon(
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(24.dp, 24.dp),
    tint: Color = Color. Unspecified,
    descriptor: String
    ){
    Icon(modifier = modifier.size(size),
        painter = painterResource(R.drawable.ic_add),
        contentDescription = descriptor,
        tint = tint
    )
}

@Composable
fun MoreIcon(
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(24.dp, 24.dp),
    tint: Color = Color. Unspecified,
    descriptor: String
){
    Icon(modifier = modifier.size(size),
        painter = painterResource(R.drawable.ic_menu),
        contentDescription = descriptor,
        tint = tint
    )
}

@Composable
fun BackIcon(
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(24.dp, 24.dp),
    tint: Color = Color. Unspecified,
    descriptor: String
){
    Icon(modifier = modifier.size(size),
        painter = painterResource(R.drawable.ic_arrow_back),
        contentDescription = descriptor,
        tint = tint
    )
}

// Modifier : 컴포넌트 설정
// size :     Icon 크기 설정
// tine :     Icon 내부 설정
// descriptor : Icon 에 대한 설명
// Icon 함수 : Icon 사용을 위한 함수