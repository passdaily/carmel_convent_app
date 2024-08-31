package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ChapterListModel(
    @SerializedName("ChapterList")
    val chapterList: ArrayList<Chapter>
){
    @Keep
    data class Chapter(
        @SerializedName("CHAPTER_ID")
        val cHAPTERID: Int,
        @SerializedName("CHAPTER_NAME")
        val cHAPTERNAME: String,
        @SerializedName("CHAPTER_STATUS")
        val cHAPTERSTATUS: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int
    )
}