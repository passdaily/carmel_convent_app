package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class YoutubeReportModel(
    @SerializedName("YoutubeDetails")
    val youtubeDetails: List<YoutubeDetail>,
    @SerializedName("YoutubeLogList")
    val youtubeLogList: List<YoutubeLog>
){
    @Keep
    data class YoutubeLog(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("FIRST_DATE")
        val fIRSTDATE: String,
        @SerializedName("LOG_COUNT")
        val lOGCOUNT: Int,
        @SerializedName("RECENT_DATE")
        val rECENTDATE: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("YOUTUBE_ID")
        val yOUTUBEID: Int,
        @SerializedName("YOUTUBE_LOG_ID")
        val yOUTUBELOGID: Int
    )


    @Keep
    data class YoutubeDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CHAPTER_ID")
        val cHAPTERID: Int,
        @SerializedName("CHAPTER_NAME")
        val cHAPTERNAME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("YOUTUBE_CREATED_BY")
        val yOUTUBECREATEDBY: Int,
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