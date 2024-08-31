package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class YoutubeListModel(
    @SerializedName("YoutubeList")
    val youtubeList: List<Youtube>
){
    data class Youtube(
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CHAPTER_NAME")
        val cHAPTERNAME: String,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("ORGINAL_FILE")
        val oRGINALFILE: String,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
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
        val yOUTUBETITLE: String,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String
    )

    //YOUTUBE_ID: 19,
    //ACCADEMIC_ID: 8,
    //CLASS_ID: 1,
    //SUBJECT_ID: null,
    //CHAPTER_ID: null,
    //YOUTUBE_TITLE: "kingdom of plants",
    //YOUTUBE_DESCRIPTION: "let's know our nature",
    //YOUTUBE_LINK: "https://youtu.be/IYxfz1PSfZ0",
    //YOUTUBE_SORT_ORDER: null,
    //YOUTUBE_DATE: "2022-05-31T12:46:19.897",
    //YOUTUBE_CREATED_BY: "GAFOOR",
    //YOUTUBE_STATUS: 1,
    //CLASS_NAME: "X-A",
    //SUBJECT_NAME: "Biology",
    //ACCADEMIC_TIME: "2021-2022",
    //CHAPTER_NAME: "1.Sensations and Responses",
    //ORGINAL_FILE: "0",
}