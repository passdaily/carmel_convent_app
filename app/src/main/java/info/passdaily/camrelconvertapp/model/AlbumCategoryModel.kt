package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AlbumCategoryModel(
    @SerializedName("AlbumCategory")
    val albumCategory: List<AlbumCategory>
){
    @Keep
    data class AlbumCategory(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ALBUM_CATEGORY_CREATED")
        val aLBUMCATEGORYCREATED: Int,
        @SerializedName("ALBUM_CATEGORY_DATE")
        val aLBUMCATEGORYDATE: String,
        @SerializedName("ALBUM_CATEGORY_DISCRIPTION")
        val aLBUMCATEGORYDISCRIPTION: String,
        @SerializedName("ALBUM_CATEGORY_ID")
        val aLBUMCATEGORYID: Int,
        @SerializedName("ALBUM_CATEGORY_NAME")
        val aLBUMCATEGORYNAME: String,
        @SerializedName("ALBUM_CATEGORY_STATUS")
        val aLBUMCATEGORYSTATUS: Int,
        @SerializedName("ALBUM_CATEGORY_TYPE")
        val aLBUMCATEGORYTYPE: String?
    )
}