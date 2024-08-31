package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ChaptersListStaffModel(
    @SerializedName("ChaptersList")
    val chaptersList: List<Chapters>
){
    @Keep
    data class Chapters(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID:  Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME:  String?,
        @SerializedName("CHAPTER_ID")
        val cHAPTERID: Int,
        @SerializedName("CHAPTER_NAME")
        val cHAPTERNAME:  String,
        @SerializedName("CHAPTER_STATUS")
        val cHAPTERSTATUS: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME:  String?,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String?
    )

    //CHAPTER_ID: 257,
    //ACCADEMIC_ID: 8,
    //CLASS_ID: 2,
    //SUBJECT_ID: 9,
    //CHAPTER_NAME: "2.Windows of knowledge ",
    //CHAPTER_STATUS: 1,
    //CLASS_NAME: "X-B",
    //SUBJECT_NAME: "Biology",
    //ACCADEMIC_TIME: "2021-2022",
}