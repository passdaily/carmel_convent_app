package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AttendanceSummaryModel(
    @SerializedName("AttendanceSummary")
    val attendanceSummary: List<AttendanceSummary>
){
    @Keep
    data class AttendanceSummary(
        @SerializedName("ATTENDANCE_PERCENTAGE")
        val aTTENDANCEPERCENTAGE: Int,
        @SerializedName("ATTENDANCE_TAKEN")
        val aTTENDANCETAKEN: Int,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("TOTAL_ABSENT")
        val tOTALABSENT: Int,
        @SerializedName("TOTAL_PRESENT")
        val tOTALPRESENT: Int,
        @SerializedName("TOTAL_WORKING")
        val tOTALWORKING: Int
    )
}