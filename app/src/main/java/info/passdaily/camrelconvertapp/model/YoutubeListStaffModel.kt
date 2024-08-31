package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class YoutubeListStaffModel(
    @SerializedName("YoutubeList")
    val youtubeList: ArrayList<Youtube>
){
    @Keep
    data class Youtube(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CHAPTER_ID")
        val cHAPTERID: Any,
        @SerializedName("CHAPTER_NAME")
        val cHAPTERNAME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("ORGINAL_FILE")
        val oRGINALFILE: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Any,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String?,
        @SerializedName("YOUTUBE_CREATED_BY")
        val yOUTUBECREATEDBY: String,
        @SerializedName("YOUTUBE_DATE")
        val yOUTUBEDATE: String,
        @SerializedName("YOUTUBE_DESCRIPTION")
        val yOUTUBEDESCRIPTION: String,
        @SerializedName("YOUTUBE_ID")
        val yOUTUBEID: Int,
        @SerializedName("YOUTUBE_LINK")
        val yOUTUBELINK: String,
        @SerializedName("YOUTUBE_SORT_ORDER")
        val yOUTUBESORTORDER: Any,
        @SerializedName("YOUTUBE_STATUS")
        val yOUTUBESTATUS: Int,
        @SerializedName("YOUTUBE_TITLE")
        val yOUTUBETITLE: String
    )
}