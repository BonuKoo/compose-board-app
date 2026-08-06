package com.bonukoo.board.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.bonukoo.board.BuildConfig
import com.bonukoo.board.server.service.BoardService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    // 서버에서 JSON을 받아 변환
    private var gson: Gson? = null
    private var retrofit : Retrofit? = null

    private var boardService: BoardService? = null

    fun getGson(): Gson{
        if (gson==null) {
            gson = GsonBuilder()
                .setLenient()
                .create()
        }
        return gson!!
    }
    /*baseUrl로 전송하는데, converting 할 때 Gson을 써라.
    * baseUrl에는, 우리가 만든 서버의 도메인이 들어가야 한다.
    * */
    fun getRetrofit(): Retrofit{
        if (retrofit == null){
           retrofit = Retrofit
               .Builder()
               // local.properties 의 server.base.url 값이 빌드 시 주입된다.
               // 주의, localhost는 인식을 못한다.
               .baseUrl(BuildConfig.SERVER_BASE_URL)
               .addConverterFactory(
                   GsonConverterFactory
                       .create(getGson()
                       )
               )
               .build()
        }
        return retrofit!!
    }

    /** getRetrofit으로 BoardSerivce를 주입*/
    fun getBoardService(): BoardService{
        if (boardService == null){
            boardService = getRetrofit().create(BoardService::class.java)
        }
        return boardService!!
    }
}

