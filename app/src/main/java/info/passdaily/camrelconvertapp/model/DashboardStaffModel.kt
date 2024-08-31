package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DashboardStaffModel(
    @SerializedName("EventList")
    val eventList: List<Event>,
    @SerializedName("InboxList")
    val inboxList: List<Inbox>,
    @SerializedName("Tiles")
    val tiles: Tiles,
    @SerializedName("Updates")
    val updates: List<Update>,
    @SerializedName("ProfileDetails")
    val profileDetails: List<ProfileDetails>,
){

    @Keep
    data class Update(
        @SerializedName("UPDATES_DATE")
        val uPDATESDATE: String,
        @SerializedName("UPDATES_DESCRIPTION")
        val uPDATESDESCRIPTION: String,
        @SerializedName("UPDATES_ID")
        val uPDATESID: Int,
        @SerializedName("UPDATES_STATUS")
        val uPDATESSTATUS: Int,
        @SerializedName("UPDATES_TITLE")
        val uPDATESTITLE: String
    )

    @Keep
    data class Inbox(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("STAFF_ID")
        val sTAFFID: Int,
        @SerializedName("VIRTUAL_MAIL_CONTENT")
        val vIRTUALMAILCONTENT: String,
        @SerializedName("VIRTUAL_MAIL_READ_DATE")
        val vIRTUALMAILREADDATE: String,
        @SerializedName("VIRTUAL_MAIL_READ_STATUS")
        val vIRTUALMAILREADSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_BY")
        val vIRTUALMAILSENTBY: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_DATE")
        val vIRTUALMAILSENTDATE: String,
        @SerializedName("VIRTUAL_MAIL_SENT_ID")
        val vIRTUALMAILSENTID: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_STAFF_ID")
        val vIRTUALMAILSENTSTAFFID: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_STATUS")
        val vIRTUALMAILSENTSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_TITLE")
        val vIRTUALMAILTITLE: String,
        @SerializedName("VIRTUAL_MAIL_TYPE")
        val vIRTUALMAILTYPE: Int
    )

    @Keep
    data class Event(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("EVENT_DATE")
        val eVENTDATE: String,
        @SerializedName("EVENT_DESCRIPTION")
        val eVENTDESCRIPTION: String,
        @SerializedName("EVENT_FILE")
        val eVENTFILE: String,
        @SerializedName("EVENT_ID")
        val eVENTID: Int,
        @SerializedName("EVENT_LINK_FILE")
        val eVENTLINKFILE: String,
        @SerializedName("EVENT_STATUS")
        val eVENTSTATUS: Int,
        @SerializedName("EVENT_TITLE")
        val eVENTTITLE: String,
        @SerializedName("EVENT_TYPE")
        val eVENTTYPE: Int
    )

    @Keep
    data class Tiles(
        @SerializedName("ADMIN_STATUS")
        val aDMINSTATUS: Int,
        @SerializedName("INBOX_COUNT")
        val iNBOXCOUNT: Int,
        @SerializedName("TOTAL_ABSENT_LASTWEEK")
        val tOTALABSENTLASTWEEK: Int,
        @SerializedName("TOTAL_ATTENDANCE_TAKEN")
        val tOTALATTENDANCETAKEN: Int,
        @SerializedName("TOTAL_ATTENDANCE_TAKEN_SHARP")
        val tOTALATTENDANCETAKENSHARP: Int,
        @SerializedName("TOTAL_CLASS")
        val tOTALCLASS: Int,
        @SerializedName("TOTAL_CONVEYORS")
        val tOTALCONVEYORS: Int,
        @SerializedName("TOTAL_ENQUIRY")
        val tOTALENQUIRY: Int,
        @SerializedName("TOTAL_LEAVE_NOTE")
        val tOTALLEAVENOTE: Int,
        @SerializedName("TOTAL_PTA")
        val tOTALPTA: Int,
        @SerializedName("TOTAL_STAFF")
        val tOTALSTAFF: Int,
        @SerializedName("TOTAL_STUDENT")
        val tOTALSTUDENT: Int
    )

    @Keep
    data class ProfileDetails(
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("STAFF_ID")
        val sTAFFID: Int,
        @SerializedName("ADMIN_ROLE")
        val aDMINROLE: Int,
        @SerializedName("ADMIN_ROLE_NAME")
        val aDMINROLENAME: String,
        @SerializedName("LOGIN_ROLE")
        val lOGINROLE: String,
        @SerializedName("LOGIN_STATUS")
        val lOGINSTATUS: String,
        @SerializedName("SCHOOL_ID")
        val sCHOOLID: Int,
        @SerializedName("STAFF_FNAME")
        val sTAFFFNAME: String,
        @SerializedName("STAFF_IMAGE")
        val sTAFFIMAGE: String?,
        //yOUTUBELINK
        @SerializedName("YOUTUBE_LINK")
        val yOUTUBELINK: String?
    )

    ///"ProfileDetails": [
    //{
    //"ADMIN_ID": 1,
    //"STAFF_ID": 1,
    //"ADMIN_ROLE": 1,
    //"ADMIN_ROLE_NAME": "ADMIN",
    //"LOGIN_ROLE": "ADMIN",
    //"LOGIN_STATUS": "SUCCESS",
    //"SCHOOL_ID": 1,
    //"STAFF_FNAME": "ANVER S",
    //"STAFF_IMAGE": ""
    //}
    //]
}