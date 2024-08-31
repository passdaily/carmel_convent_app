package info.passdaily.camrelconvertapp.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class ClassSubjectDetailModel(
    @SerializedName("ClassSubjectDetails")
    val classSubjectDetails: ArrayList<ClassSubjectDetail>
){

    @Keep
    data class ClassSubjectDetail(
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CLASS_SECTION")
        val cLASSSECTION: String,
        @SerializedName("CLASS_SUBJECT_ID")
        val cLASSSUBJECTID: String,
        @SerializedName("CLASS_SUBJECT_NAME")
        val cLASSSUBJECTNAME: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_STATUS")
        val sUBJECTSTATUS: Int,
        var selectedValue : Boolean = false
    )

}
