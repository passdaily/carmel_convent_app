package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PtaListModel(
    @SerializedName("PtaList")
    val ptaList: List<Pta>
){
    @Keep
    data class Pta(
        @SerializedName("PTA_MEMBER_EMAIL")
        val pTAMEMBEREMAIL: String,
        @SerializedName("PTA_MEMBER_ID")
        val pTAMEMBERID: Int,
        @SerializedName("PTA_MEMBER_IMAGE")
        val pTAMEMBERIMAGE: String,
        @SerializedName("PTA_MEMBER_MOBILE")
        val pTAMEMBERMOBILE: String,
        @SerializedName("PTA_MEMBER_NAME")
        val pTAMEMBERNAME: String,
        @SerializedName("PTA_MEMBER_ROLE")
        val pTAMEMBERROLE: Int,
        @SerializedName("PTA_MEMBER_STATUS")
        val pTAMEMBERSTATUS: Int,
        var isChecked : Boolean = false
    )
}