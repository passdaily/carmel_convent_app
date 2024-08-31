package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class InboxListStaffModel(
    @SerializedName("InboxList")
    val inboxList: List<Inbox>
){
    @Keep
    data class Inbox(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("STAFF_ID")
        val sTAFFID: Int,
        @SerializedName("VIRTUAL_MAIL_CONTENT")
        val vIRTUALMAILCONTENT: String,
        @SerializedName("VIRTUAL_MAIL_READ_DATE")
        val vIRTUALMAILREADDATE: Any,
        @SerializedName("VIRTUAL_MAIL_READ_STATUS")
        var vIRTUALMAILREADSTATUS: Int,
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
        val vIRTUALMAILTYPE: Int,
        @SerializedName("FILE_COUNT")
        val fILECOUNT: Int,
//        FILE_COUNT
    )
}