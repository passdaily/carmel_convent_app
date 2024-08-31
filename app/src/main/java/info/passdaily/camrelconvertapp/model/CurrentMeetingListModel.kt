package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class CurrentMeetingListModel(
    @SerializedName("MeetingList")
    var meetingList: List<Meeting>
) {
    @Keep
    data class Meeting(
        @SerializedName("ACCADEMIC_ID")
        var aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        var aDMINID: Int,
        @SerializedName("CLASS_ID")
        var cLASSID: Int,
        @SerializedName("CLASS_NAME")
        var cLASSNAME: String,
        @SerializedName("CREATED_DATE")
        var cREATEDDATE: String,
        @SerializedName("DATE")
        var dATE: Any,
        @SerializedName("SUBJECT_ID")
        var sUBJECTID: Int,
        @SerializedName("SUBJECT_ID_SUB")
        var sUBJECTIDSUB: Int,
        @SerializedName("SUBJECT_NAME")
        var sUBJECTNAME: String,
        @SerializedName("TOTAL_ATTEND_STUDENT")
        var tOTALATTENDSTUDENT: Int,
        @SerializedName("TOTAL_STUDENT")
        var tOTALSTUDENT: Int,
        @SerializedName("UPDATED_DATE")
        var uPDATEDDATE: Any,
        @SerializedName("Z_LIVE_CLASS_ID")
        var zLIVECLASSID: Int,
        @SerializedName("ZOOM_CREATED_BY")
        var zOOMCREATEDBY: String,
        @SerializedName("SUBJECT_ICON")
        var sUBJECTICON: String,
        @SerializedName("ZOOM_END_DATE")
        var zOOMENDDATE: Any,
        @SerializedName("ZOOM_MEETING_ID")
        var zOOMMEETINGID: String,
        @SerializedName("ZOOM_MEETING_LINK")
        var zOOMMEETINGLINK: String,
        @SerializedName("ZOOM_MEETING_PASSWORD")
        var zOOMMEETINGPASSWORD: String,
        @SerializedName("ZOOM_MEETING_STATUS")
        var zOOMMEETINGSTATUS: Int,
        @SerializedName("ZOOM_START_DATE")
        var zOOMSTARTDATE: String,
        @SerializedName("Z_STATUS")
        var zSTATUS: Int
    )
}