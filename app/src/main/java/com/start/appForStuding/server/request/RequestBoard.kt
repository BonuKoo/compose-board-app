package com.start.appForStuding.server.request

data class RequestBoard(
    val id: Int,
    val title: String,
    val content: String,
    val name: String
)
// 반드시 서버에서 받아오는 Json 값과 일치시켜줘야 한다.