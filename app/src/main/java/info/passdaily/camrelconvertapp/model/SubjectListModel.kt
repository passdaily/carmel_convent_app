package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class SubjectListModel(
    @SerializedName("SubjectList")
    val subjectList: List<Subject>
){
    @Keep
    data class Subject(
        @SerializedName("SUBJECT_CAT_ID")
        val sUBJECTCATID: Int,
        @SerializedName("SUBJECT_CAT_NAME")
        val sUBJECTCATNAME: String,
        @SerializedName("SUBJECT_CODE")
        val sUBJECTCODE: String,
        @SerializedName("SUBJECT_DESCRIPTION")
        val sUBJECTDESCRIPTION: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_STATUS")
        val sUBJECTSTATUS: Int
    )
}