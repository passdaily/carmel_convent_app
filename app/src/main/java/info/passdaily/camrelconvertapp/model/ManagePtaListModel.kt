package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ManagePtaListModel(
    @SerializedName("PtaList")
    val ptaList: List<Pta>
){
    @Keep
    data class Pta(
        @SerializedName("PTA_MEMBER_EMAIL")
        var pTAMEMBEREMAIL: String,
        @SerializedName("PTA_MEMBER_ID")
        var pTAMEMBERID: Int,
        @SerializedName("PTA_MEMBER_IMAGE")
        var pTAMEMBERIMAGE: String,
        @SerializedName("PTA_MEMBER_MOBILE")
        var pTAMEMBERMOBILE: String,
        @SerializedName("PTA_MEMBER_NAME")
        var pTAMEMBERNAME: String,
        @SerializedName("PTA_MEMBER_ROLE")
        var pTAMEMBERROLE: Int,
        @SerializedName("PTA_MEMBER_STATUS")
        var pTAMEMBERSTATUS: Int,
        @SerializedName("PTA_MEMBER_ADDRESS")
        var pTAMEMBERADDRESS: String,

        var updated : Boolean = false
    )
}