package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ZoomScheduleReportListModel(
    @SerializedName("ZoomReportList")
    val zoomReportList: List<ZoomReport>
){
    @Keep
    data class ZoomReport(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLICK_COUNT")
        val cLICKCOUNT: Int,
        @SerializedName("MEETING_CLICK_DATE")
        val mEETINGCLICKDATE: String,
        @SerializedName("MEETING_RECENT_DATE")
        val mEETINGRECENTDATE: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("Z_MEETING_ID")
        val zMEETINGID: Int,
        @SerializedName("ZR_MEETING_ID")
        val zRMEETINGID: Int
    )
}