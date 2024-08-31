package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ExamGradeMspModel(
    @SerializedName("ExamGrade")
    val examGrade: List<ExamGrade>
){
    @Keep
    data class ExamGrade(
        @SerializedName("GRADE_A1")
        val gRADEA1: Int,
        @SerializedName("GRADE_A2")
        val gRADEA2: Int,
        @SerializedName("GRADE_B1")
        val gRADEB1: Int,
        @SerializedName("GRADE_B2")
        val gRADEB2: Int,
        @SerializedName("GRADE_C1")
        val gRADEC1: Int,
        @SerializedName("GRADE_C2")
        val gRADEC2: Int,
        @SerializedName("GRADE_D")
        val gRADED: Int,
        @SerializedName("GRADE_E")
        val gRADEE: Int,
        @SerializedName("OUTOFF_MARK")
        val oUTOFFMARK: Int,
        @SerializedName("OUTOFF_MARK_CE")
        val oUTOFFMARKCE: Int,
        @SerializedName("PASS_MARK")
        val pASSMARK: Int,
        @SerializedName("PASS_MARK_CE")
        val pASSMARKCE: Int,
        @SerializedName("STAFF_FNAME")
        val sTAFFFNAME: String?,
        @SerializedName("SUBJECT_CODE")
        val sUBJECTCODE: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_TOTAL_MARK")
        val sUBJECTTOTALMARK: Any,
        @SerializedName("TOTAL_ATTEND")
        val tOTALATTEND: Int
    )
}