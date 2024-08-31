package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NotificationSentStaffModel(
    @SerializedName("InboxSentList")
    val inboxSentList: List<InboxSent>
){
    @Keep
    data class InboxSent(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CLASS_SECTION")
        val cLASSSECTION: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        val sTUDENTGUARDIANNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("VIRTUAL_MAIL_CONTENT")
        val vIRTUALMAILCONTENT: String,
        @SerializedName("VIRTUAL_MAIL_SENT_BY")
        val vIRTUALMAILSENTBY: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_DATE")
        val vIRTUALMAILSENTDATE: String,
        @SerializedName("VIRTUAL_MAIL_SENT_ID")
        val vIRTUALMAILSENTID: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_STATUS")
        val vIRTUALMAILSENTSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_TITLE")
        val vIRTUALMAILTITLE: String,
        @SerializedName("VIRTUAL_MAIL_TYPE")
        val vIRTUALMAILTYPE: Int
    )
}