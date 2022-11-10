package io.github.mumu12641.lark.network

import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.entity.network.netease.*
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.MUSIC_QUALITY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("cloudsearch?type=100")
    fun getSearchArtistResponse(@Query("keywords") keywords: String): Call<SearchArtistResponse>

    @GET("artist/detail")
    fun getArtistDetail(@Query("id") id: Long): Call<ArtistDetailsResponse>

    @GET("login/cellphone")
    fun cellphoneLogin(
        @Query("phone") phoneNumber: String,
        @Query("password") Password: String
    ): Call<NeteaseUser>

    @GET("user/detail")
    fun getUserDetail(
        @Query("uid") uid: Long,
        @Query("cookie") cookie: String = kv.decodeString(
            "Cookie",
            "null"
        )!!,
    ): Call<UserDetail>

    @GET("search/hot/detail")
    fun getSearchHot(): SearchHot

    @GET("logout")
    fun logout(): Call<Any>

    @GET("banner?type=1")
    fun getBanner(): Call<Banner>

    @GET("song/url")
    fun getSongUrl(
        @Query("id") id: Long,
        @Query("cookie") cookie: String = kv.decodeString(
            "Cookie",
            "null"
        )!!,
    ): Call<SongUrl>

    @GET("song/detail")
    fun getSongDetail(@Query("ids") ids: String): Call<SongDetail>

    @GET("/playlist/detail")
    fun getNeteaseSongList(
        @Query("id") id: Long,
        @Query("cookie") cookie: String = kv.decodeString(
            "Cookie",
            "null"
        )!!,
    ): Call<NeteaseSongList>

    @GET("/playlist/track/all")
    fun getNeteaseSongListTracks(
        @Query("id") id: Long,
        @Query("cookie") cookie: String = kv.decodeString(
            "Cookie",
            "null"
        )!!,
    ): Call<Tracks>

    @GET("/cloudsearch")
    fun getSearchSongResponse(@Query("keywords") keywords: String): SearchSongResponse

    @GET("/lyric")
    fun getLyric(@Query("id") id: Long): Call<Lyrics>


    @GET("/song/url/v1")
    fun getLevelMusic(
        @Query("id") id: Long,
        @Query("level") level: String = kv.decodeString(
            MUSIC_QUALITY,
            "standard"
        )!!,
        @Query("cookie") cookie: String = kv.decodeString("Cookie", "null")!!,
    ): Call<SongUrl>

    @GET("/register/anonimous")
    fun anonymousLogin(): Call<Anonymous>
}