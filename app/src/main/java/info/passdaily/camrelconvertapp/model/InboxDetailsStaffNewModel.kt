package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class InboxDetailsStaffNewModel(
    @SerializedName("FileDetails")
    var fileDetails: List<FileDetail>,
    @SerializedName("InboxDetails")
    var inboxDetails: InboxDetails
) {
    @Keep
    data class FileDetail(
        @SerializedName("CREATED_BY")
        var cREATEDBY: Int,
        @SerializedName("CREATED_DATE")
        var cREATEDDATE: String,
        @SerializedName("FILE_ID")
        var fILEID: Int,
        @SerializedName("FILE_NAME")
        var fILENAME: String,
        @SerializedName("FILE_STATUS")
        var fILESTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_ID")
        var vIRTUALMAILID: Int
    )

    @Keep
    data class InboxDetails(
        @SerializedName("ACCADEMIC_ID")
        var aCCADEMICID: Int,
        @SerializedName("CREATED_STAFF")
        var cREATEDSTAFF: Any,
        @SerializedName("FILE_COUNT")
        var fILECOUNT: Int,
        @SerializedName("STAFF_ID")
        var sTAFFID: Int,
        @SerializedName("VIRTUAL_MAIL_CONTENT")
        var vIRTUALMAILCONTENT: String,
        @SerializedName("VIRTUAL_MAIL_READ_DATE")
        var vIRTUALMAILREADDATE: String,
        @SerializedName("VIRTUAL_MAIL_READ_STATUS")
        var vIRTUALMAILREADSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_BY")
        var vIRTUALMAILSENTBY: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_DATE")
        var vIRTUALMAILSENTDATE: String,
        @SerializedName("VIRTUAL_MAIL_SENT_ID")
        var vIRTUALMAILSENTID: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_STAFF_ID")
        var vIRTUALMAILSENTSTAFFID: Int,
        @SerializedName("VIRTUAL_MAIL_SENT_STATUS")
        var vIRTUALMAILSENTSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_TITLE")
        var vIRTUALMAILTITLE: String,
        @SerializedName("VIRTUAL_MAIL_TYPE")
        var vIRTUALMAILTYPE: Int
    )
}