package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ZoomMeetingAttendedListModel(
    @SerializedName("MeetingAttendedList")
    val meetingAttendedList: List<MeetingAttended>
){
    @Keep
    data class MeetingAttended(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("SUBJECT_ID_SUB")
        val sUBJECTIDSUB: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("Z_ATTENT_TOTAL_TIME")
        val zATTENTTOTALTIME: Int,
        @SerializedName("Z_LIVE_ATTENT_ID")
        val zLIVEATTENTID: Int,
        @SerializedName("Z_LIVE_CLASS_ID")
        val zLIVECLASSID: Int,
        @SerializedName("ZOOM_CREATED_BY")
        val zOOMCREATEDBY: String,
        @SerializedName("ZOOM_END_DATE")
        val zOOMENDDATE: Any,
        @SerializedName("ZOOM_MEETING_STATUS")
        val zOOMMEETINGSTATUS: Int,
        @SerializedName("ZOOM_RECENT_DATE")
        val zOOMRECENTDATE: String,
        @SerializedName("ZOOM_START_DATE")
        val zOOMSTARTDATE: String,
        @SerializedName("Z_STATUS")
        val zSTATUS: Int
    )
}