package io.github.mumu12641.lark.entity.network

import com.google.gson.annotations.SerializedName



data class SearchArtistResponse(val code: Int, val result: SearchResult){

    data class SearchResult(val artistCount: Int, val artists: List<StdArtistData>)

    data class StdArtistData(
        val name: String,
        @SerializedName("id") val artistId: Long?,
        val picUrl: String
    )

}

data class ArtistDetailsResponse(val code: Int,val data: ArtistDetailsData){
    data class ArtistDetailsData(
        val artist: Artist,
    )

    data class Artist(
        val briefDesc: String,
        val cover: String,
        val id: Int,
        val name: String,
    )
}


