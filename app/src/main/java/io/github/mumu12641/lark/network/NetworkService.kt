package io.github.mumu12641.lark.network

import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.entity.network.*
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.MUSIC_QUALITY
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("cloudsearch?type=100")
    suspend fun getSearchArtistResponse(@Query("keywords") keywords: String): SearchArtistResponse

    @GET("artist/detail")
    suspend fun getArtistDetail(@Query("id") id: Long): ArtistDetailsResponse

    @GET("login/cellphone")
    suspend fun cellphoneLogin(
        @Query("phone") phoneNumber: String,
        @Query("password") Password: String
    ): NeteaseUser

    @GET("user/detail")
    suspend fun getUserDetail(@Query("uid") uid: Long): UserDetail

    @GET("search/hot/detail")
    suspend fun getSearchHot(): SearchHot

    @GET("logout")
    suspend fun logout(): Any

    @GET("banner?type=1")
    suspend fun getBanner(): Banner

    @GET("song/url")
    suspend fun getSongUrl(@Query("id") id: Long): SongUrl

    @GET("song/detail")
    suspend fun getSongDetail(@Query("ids") ids: String): SongDetail

    @GET("/playlist/detail")
    suspend fun getNeteaseSongList(@Query("id") id: Long): NeteaseSongList

    @GET("/playlist/track/all")
    suspend fun getNeteaseSongListTracks(@Query("id") id: Long): Tracks

    @GET("/cloudsearch")
    suspend fun getSearchSongResponse(@Query("keywords") keywords: String): SearchSongResponse

    @GET("/lyric")
    suspend fun getLyric(@Query("id") id: Long): Lyrics

    @GET("/check/music")
    suspend fun getCheckMusic(@Query("id") id: Long): MusicCheck

    @GET("/song/url/v1")
    suspend fun getLevelMusic(
        @Query("id") id: Long,
        @Query("level") level: String = kv.decodeString(
            MUSIC_QUALITY,
            "standard"
        )!!
    ): SongUrl

    @GET("/register/anonimous")
    suspend fun anonymousLogin(): Any
}