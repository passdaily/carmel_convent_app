package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ObjectiveOnlineExamStatus(
    @SerializedName("ELAPSED_TIME")
    val eLAPSEDTIME: Int,
    @SerializedName("JSON_ERROR")
    val jSONERROR: String,
    @SerializedName("OEXAM_ATTEMPT_ID")
    val oEXAMATTEMPTID: Int,
    @SerializedName("OEXAM_DURATION")
    val oEXAMDURATION: Long,
    @SerializedName("RESULT")
    val rESULT: String
)