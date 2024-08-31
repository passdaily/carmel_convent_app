package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MessageListModel(
    @SerializedName("MessageList")
    val messageList: List<Message>
){
    @Keep
    data class Message(
        @SerializedName("MESSAGE_CONTENT")
        val mESSAGECONTENT: String,
        @SerializedName("MESSAGE_CREATED_BY")
        val mESSAGECREATEDBY: String,
        @SerializedName("MESSAGE_DATE")
        val mESSAGEDATE: String,
        @SerializedName("MESSAGE_ID")
        val mESSAGEID: Int,
        @SerializedName("MESSAGE_STATUS")
        val mESSAGESTATUS: Int,
        @SerializedName("MESSAGE_TITLE")
        val mESSAGETITLE: String
    )
}