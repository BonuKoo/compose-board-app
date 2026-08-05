package com.start.appForStuding.server.service

import com.start.appForStuding.server.request.RequestBoard
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BoardService {

    /* suspend fun : 비동기적 호출 */
    @POST("/board")
    suspend fun createBoard(
        //@Body : 파라미터를 받아 올 Body를 명명
        @Body requestBody: RequestBoard
    ): Unit

    @GET("/board")
    suspend fun getBoardList() : List<RequestBoard>

    @GET("/board/{id}")
    suspend fun getBoardById(
        @Path("id") id: Int
    ) : RequestBoard

    @PATCH("/board")
    suspend fun updateBoard(
        @Body requestBody: RequestBoard
    ) : Unit

    @DELETE("/board/{id}")
    suspend fun deleteBoard(
        @Path("id") id: Int
    ) : Unit

    /* call : 동기적 호출
    @POST("/board")
     fun createBoard2(
        @Body requestBody: RequestBoard
    ): Call<Unit>
    */
}

// interFace에서 만든 함수는
// Retrofit Builder에서 구현한다.
// Unit : 결과가 없을 때.