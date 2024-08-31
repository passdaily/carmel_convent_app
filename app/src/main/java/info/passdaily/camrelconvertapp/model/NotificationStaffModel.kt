package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NotificationStaffModel(
    @SerializedName("InboxList")
    val inboxList: List<Inbox>
){
    @Keep
    data class Inbox(
        @SerializedName("VIRTUAL_MAIL_CONTENT")
        val vIRTUALMAILCONTENT: String,
        @SerializedName("VIRTUAL_MAIL_CREATED_BY")
        val vIRTUALMAILCREATEDBY: Int,
        @SerializedName("VIRTUAL_MAIL_DATE")
        val vIRTUALMAILDATE: String,
        @SerializedName("VIRTUAL_MAIL_ID")
        val vIRTUALMAILID: Int,
        @SerializedName("VIRTUAL_MAIL_STATUS")
        val vIRTUALMAILSTATUS: Int,
        @SerializedName("VIRTUAL_MAIL_TITLE")
        val vIRTUALMAILTITLE: String,
        @SerializedName("FILE_COUNT")
        val fileCount: Int,
        @SerializedName("CREATED_STAFF")
        val createdStaff: String,
    )
}