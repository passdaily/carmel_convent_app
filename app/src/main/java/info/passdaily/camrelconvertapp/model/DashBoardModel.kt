package info.passdaily.camrelconvertapp.model

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DashBoardModel(
    @SerializedName("AcademicDetails")
    val academicDetails: AcademicDetails,
    @SerializedName("EventDetails")
    val eventDetails: List<EventDetail>,
    @SerializedName("InboxDetails")
    val inboxDetails: List<InboxDetail>,
    @SerializedName("PeriodDetails")
    val periodDetails: List<PeriodDetail>,
    @SerializedName("SubjectDetails")
    val subjectDetails: List<SubjectDetail>,
    @SerializedName("TileDetails")
    val tileDetails: TileDetails,
    @SerializedName("VideoDetails")
    val videoDetails: List<VideoDetail>
){
    data class VideoDetail(
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String?,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String?,
        @SerializedName("TOTAL_VIDEOS")
        val tOTALVIDEOS: Int
    )
    data class TileDetails(
        @SerializedName("TOTAL_ABSENTS")
        val tOTALABSENTS: Int,
        @SerializedName("TOTAL_ASSIGNMENT")
        val tOTALASSIGNMENT: Int,
        @SerializedName("TOTAL_DESCRIPTIVE")
        val tOTALDESCRIPTIVE: Int,
        @SerializedName("TOTAL_ENQUIRY")
        val tOTALENQUIRY: Int,
        @SerializedName("TOTAL_INBOX")
        val tOTALINBOX: Int,
        @SerializedName("TOTAL_LEAVENOTES")
        val tOTALLEAVENOTES: Int,
        @SerializedName("TOTAL_LIVE_CLASS")
        val tOTALLIVECLASS: Int,
        @SerializedName("TOTAL_NOTICEBOARD")
        val tOTALNOTICEBOARD: Int,
        @SerializedName("TOTAL_OBJECTIVE")
        val tOTALOBJECTIVE: Int,
        @SerializedName("TOTAL_STUDYMATERIAL")
        val tOTALSTUDYMATERIAL: Int,
        @SerializedName("TOTAL_VIDEOS")
        val tOTALVIDEOS: Int
    )
    data class SubjectDetail(
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String?,
        @SerializedName("SUBJECT_STATUS")
        val sUBJECTSTATUS: Int
    )
    data class PeriodDetail(
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("PERIOD_NAME")
        val pERIODNAME: String?,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: Any,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String?
    )
    data class InboxDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_SECTION")
        val cLASSSECTION: String?,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("VIRTUAL_MAIL_CONTENT")
        val vIRTUALMAILCONTENT: String?,
        @SerializedName("VIRTUAL_MAIL_READ_DATE")
        val vIRTUALMAILREADDATE: Any,
        @SerializedName("VIRTUAL_MAIL_READ_STATUS")
        val vIRTUALMAILREADSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_BY")
        val vIRTUALMAILSENTBY: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_DATE")
        val vIRTUALMAILSENTDATE: String?,
        @SerializedName("VIRTUAL_MAIL_SENT_ID")
        val vIRTUALMAILSENTID: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_STATUS")
        val vIRTUALMAILSENTSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_STUNDENT_ID")
        val vIRTUALMAILSENTSTUNDENTID: Int,
        @SerializedName("VIRTUAL_MAIL_TITLE")
        val vIRTUALMAILTITLE: String?,
        @SerializedName("VIRTUAL_MAIL_TYPE")
        val vIRTUALMAILTYPE: Int
    )
    data class AcademicDetails(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String?,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String?,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        //YOUTUBE_LINK
        @SerializedName("YOUTUBE_LINK")
        val yOUTUBELINK: String?,
    )
    data class EventDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("EVENT_DATE")
        val eVENTDATE: String?,
        @SerializedName("EVENT_DESCRIPTION")
        val eVENTDESCRIPTION: String?,
        @SerializedName("EVENT_FILE")
        val eVENTFILE: String?,
        @SerializedName("EVENT_ID")
        val eVENTID: Int,
        @SerializedName("EVENT_LINK_FILE")
        val eVENTLINKFILE: String?,
        @SerializedName("EVENT_STATUS")
        val eVENTSTATUS: Int,
        @SerializedName("EVENT_TITLE")
        val eVENTTITLE: String?,
        @SerializedName("EVENT_TYPE")
        val eVENTTYPE: Int
    )
}