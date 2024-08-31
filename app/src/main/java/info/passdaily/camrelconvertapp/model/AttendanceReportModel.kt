package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AttendanceReportModel(
    @SerializedName("AttendanceReport")
    val attendanceReport: List<AttendanceReport>
){
    @Keep
    data class AttendanceReport(
        @SerializedName("ABSENT_DATE")
        val aBSENTDATE: String,
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_NAME")
        val aCCADEMICNAME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("DAY_NAME")
        val dAYNAME: String,
        @SerializedName("MONTH_NAME")
        val mONTHNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int
    )
}