package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class SubCategoryListModel(
    @SerializedName("subjectcategory")
    val subjectCategory: List<Subjectcategory>
){
    @Keep
    data class Subjectcategory(
        @SerializedName("SUBJECT_CAT_ID")
        val sUBJECTCATID: Int,
        @SerializedName("SUBJECT_CAT_NAME")
        val sUBJECTCATNAME: String,
        @SerializedName("SUBJECT_CAT_ORDER")
        val sUBJECTCATORDER: Int,
        @SerializedName("SUBJECT_CAT_STATUS")
        val sUBJECTCATSTATUS: Any
    )
}