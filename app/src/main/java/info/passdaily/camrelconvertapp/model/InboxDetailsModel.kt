package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class InboxDetailsModel(
    @SerializedName("InboxDetails")
    val inboxDetails: ArrayList<InboxDetail>
){
    @Keep
    data class InboxDetail(
        @SerializedName("VIRTUAL_MAIL_SENT_STUDENT_ID")
        val vIRTUALMAILSENTSTUDENTID: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_ID")
        val vIRTUALMAILSENTID: Int,
        @SerializedName("VIRTUAL_MAIL_TITLE")
        val vIRTUALMAILTITLE: String,
        @SerializedName("VIRTUAL_MAIL_CONTENT")
        val vIRTUALMAILCONTENT: String,
        @SerializedName("VIRTUAL_MAIL_TYPE")
        val vIRTUALMAILTYPE: Int,
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_SECTION")
        val cLASSSECTION: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_DATE")
        val vIRTUALMAILSENTDATE: String,
        @SerializedName("VIRTUAL_MAIL_SENT_BY")
        val vIRTUALMAILSENTBY: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_STATUS")
        val vIRTUALMAILSENTSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_READ_DATE")
        val vIRTUALMAILREADDATE: String,
        @SerializedName("VIRTUAL_MAIL_READ_STATUS")
        var vIRTUALMAILREADSTATUS: Int,
        @SerializedName("FILE_COUNT")
        var fILECOUNT: Int,
        //FILE_COUNT

    )
}