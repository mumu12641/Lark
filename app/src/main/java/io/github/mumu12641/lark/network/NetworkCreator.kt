package io.github.mumu12641.lark.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object NetworkCreator {

    //TODO https://github.com/square/retrofit/issues/3005

    private const val BASE_URL = "https://netease-cloud-music-api-self-ten.vercel.app/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val networkService:NetworkService = retrofit.create(NetworkService::class.java)
}