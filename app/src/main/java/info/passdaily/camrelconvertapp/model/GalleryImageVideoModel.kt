package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GalleryImageVideoModel(
    @SerializedName("ImageGetAll")
    var imageGetAll: List<ImageGetAll>
) {
    @Keep
    data class ImageGetAll(
        @SerializedName("ALBUM_CATEGORY_ID")
        var aLBUMCATEGORYID: Int,
        @SerializedName("ALBUM_CATEGORY_NAME")
        var aLBUMCATEGORYNAME: String,
        @SerializedName("ALBUM_CATEGORY_TYPE")
        var aLBUMCATEGORYTYPE: Int,
        @SerializedName("ALBUM_CREATED")
        var aLBUMCREATED: Int,
        @SerializedName("ALBUM_DATE")
        var aLBUMDATE: String,
        @SerializedName("ALBUM_FILE")
        var aLBUMFILE: String,
        @SerializedName("ALBUM_ID")
        var aLBUMID: Int,
        @SerializedName("ALBUM_TITLE")
        var aLBUMTITLE: String
    )
}