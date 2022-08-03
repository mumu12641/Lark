package io.github.mumu12641.lark.network

import io.github.mumu12641.lark.entity.network.ArtistDetailsResponse
import io.github.mumu12641.lark.entity.network.SearchArtistResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    //搜索歌手结果
    @GET("cloudsearch?type=100")
    suspend fun getSearchArtistResponse(@Query("keywords") keywords: String): SearchArtistResponse

    @GET("artist/detail")
    suspend fun getArtistDetail(@Query("id") id:Long): ArtistDetailsResponse
}