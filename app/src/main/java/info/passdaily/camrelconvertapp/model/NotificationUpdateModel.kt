package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NotificationUpdateModel(
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
        @SerializedName("SCHOOL_ID")
        var sCHOOLID: Int,
        @SerializedName("VIRTUAL_MAIL_CONTENT")
        var vIRTUALMAILCONTENT: String,
        @SerializedName("VIRTUAL_MAIL_CREATED_BY")
        var vIRTUALMAILCREATEDBY: Int,
        @SerializedName("VIRTUAL_MAIL_DATE")
        var vIRTUALMAILDATE: String,
        @SerializedName("VIRTUAL_MAIL_FILE")
        var vIRTUALMAILFILE: String,
        @SerializedName("VIRTUAL_MAIL_ID")
        var vIRTUALMAILID: Int,
        @SerializedName("VIRTUAL_MAIL_STATUS")
        var vIRTUALMAILSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_TITLE")
        var vIRTUALMAILTITLE: String
    )
}