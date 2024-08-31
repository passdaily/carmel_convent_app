package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class CategoryListStaffModel(
    @SerializedName("ImageCatList")
    var imageCatList: List<ImageCat>
) {
    @Keep
    data class ImageCat(
        @SerializedName("ALBUM_CATEGORY_ID")
        var aLBUMCATEGORYID: Int,
        @SerializedName("ALBUM_CATEGORY_NAME")
        var aLBUMCATEGORYNAME: String
    )
}