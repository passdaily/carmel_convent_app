package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ExamGradeStateModel(
    @SerializedName("ExamGrade")
    val examGrade: List<ExamGrade>
){

    @Keep
    data class ExamGrade(
        @SerializedName("GRADE_A")
        val gRADEA: Int,
        @SerializedName("GRADE_APLUS")
        val gRADEAPLUS: Int,
        @SerializedName("GRADE_B")
        val gRADEB: Int,
        @SerializedName("GRADE_BPLUS")
        val gRADEBPLUS: Int,
        @SerializedName("GRADE_C")
        val gRADEC: Int,
        @SerializedName("GRADE_CPLUS")
        val gRADECPLUS: Int,
        @SerializedName("GRADE_D")
        val gRADED: Int,
        @SerializedName("GRADE_DPLUS")
        val gRADEDPLUS: Int,
        @SerializedName("GRADE_E")
        val gRADEE: Int,
        @SerializedName("OUTOFF_MARK")
        val oUTOFFMARK: Int,
        @SerializedName("PASS_MARK")
        val pASSMARK: Int,
        @SerializedName("SUBJECT_CODE")
        val sUBJECTCODE: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECTWISE_PASS")
        val sUBJECTWISEPASS: Int,
        @SerializedName("TOTAL_ATTEND")
        val tOTALATTEND: Int
    )
}