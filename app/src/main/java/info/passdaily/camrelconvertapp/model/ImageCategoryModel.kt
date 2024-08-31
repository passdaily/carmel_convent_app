package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ImageCategoryModel(
    @SerializedName("ImageCatList")
    val imageCatList: List<ImageCat>
){
    @Keep
    data class ImageCat(
        @SerializedName("ALBUM_CATEGORY_ID")
        val aLBUMCATEGORYID: Int,
        @SerializedName("ALBUM_CATEGORY_NAME")
        val aLBUMCATEGORYNAME: String
    )
}