package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AlbumModel(
    @SerializedName("AlbumList")
    val albumList: ArrayList<Album>
){
    @Keep
    data class Album(
        @SerializedName("ALBUM_CATEGORY_ID")
        val aLBUMCATEGORYID: Int,
        @SerializedName("ALBUM_CATEGORY_NAME")
        val aLBUMCATEGORYNAME: String,
        @SerializedName("COUNT")
        val cOUNT: Int
    )
}