package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MessageDeliveryListModel(
    @SerializedName("SmsDeliveryList")
    val messageDeliveryList: List<SmsDelivery>
){
    @Keep
    data class SmsDelivery(
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("SMS_ACTION_CALL")
        val sMSACTIONCALL: String,
        @SerializedName("SMS_DATE")
        val sMSDATE: String,
        @SerializedName("SMS_DELIVERY_STATUS")
        val sMSDELIVERYSTATUS: Int,
        @SerializedName("SMS_LOG_ID")
        val sMSLOGID: Int,
        @SerializedName("SMS_MESSAGE")
        val sMSMESSAGE: String,
        @SerializedName("SMS_NUMBER")
        val sMSNUMBER: String,
        @SerializedName("SMS_PUSH_ID")
        val sMSPUSHID: String,
        @SerializedName("SMS_SEND_COUNT")
        val sMSSENDCOUNT: Int,
        @SerializedName("SMS_SEND_TO")
        val sMSSENDTO: String,
        @SerializedName("SMS_STATUS")
        val sMSSTATUS: Int
    )
}