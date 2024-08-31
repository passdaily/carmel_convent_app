package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GalleryImageModel(
    @SerializedName("AlbumImageList")
    val albumImageList: ArrayList<AlbumImage>
){
    @Keep
    data class AlbumImage(
        @SerializedName("ALBUM_CATEGORY_ID")
        val aLBUMCATEGORYID: Int,
        @SerializedName("ALBUM_CATEGORY_NAME")
        val aLBUMCATEGORYNAME: String,
        @SerializedName("ALBUM_CATEGORY_TYPE")
        val aLBUMCATEGORYTYPE: Int,
        @SerializedName("ALBUM_CREATED")
        val aLBUMCREATED: Int,
        @SerializedName("ALBUM_DATE")
        val aLBUMDATE: String,
        @SerializedName("ALBUM_FILE")
        val aLBUMFILE: String,
        @SerializedName("ALBUM_ID")
        val aLBUMID: Int,
        @SerializedName("ALBUM_TITLE")
        val aLBUMTITLE: String
    )
}