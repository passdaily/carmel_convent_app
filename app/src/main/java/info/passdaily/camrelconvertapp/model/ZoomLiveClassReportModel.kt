package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ZoomLiveClassReportModel(
    @SerializedName("MeetingList")
    val meetingList: List<Meeting>
){
    @Keep
    data class Meeting(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("DATE")
        val dATE: Any,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_ID_SUB")
        val sUBJECTIDSUB: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_ATTEND_STUDENT")
        val tOTALATTENDSTUDENT: Int,
        @SerializedName("TOTAL_STUDENT")
        val tOTALSTUDENT: Int,
        @SerializedName("UPDATED_DATE")
        val uPDATEDDATE: Any,
        @SerializedName("Z_LIVE_CLASS_ID")
        val zLIVECLASSID: Int,
        @SerializedName("ZOOM_CREATED_BY")
        val zOOMCREATEDBY: String,
        @SerializedName("ZOOM_END_DATE")
        val zOOMENDDATE: String,
        @SerializedName("ZOOM_MEETING_ID")
        val zOOMMEETINGID: String,
        @SerializedName("ZOOM_MEETING_LINK")
        val zOOMMEETINGLINK: String,
        @SerializedName("ZOOM_MEETING_PASSWORD")
        val zOOMMEETINGPASSWORD: String,
        @SerializedName("ZOOM_MEETING_STATUS")
        val zOOMMEETINGSTATUS: Int,
        @SerializedName("ZOOM_START_DATE")
        val zOOMSTARTDATE: String,
        @SerializedName("Z_STATUS")
        val zSTATUS: Int
    )
}