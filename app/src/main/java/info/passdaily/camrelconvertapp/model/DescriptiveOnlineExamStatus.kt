package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DescriptiveOnlineExamStatus(
    @SerializedName("ELAPSED_TIME")
    val eLAPSEDTIME: Int,
    @SerializedName("JSON_ERROR")
    val jSONERROR: String?,
    @SerializedName("EXAM_ATTEMPT_ID")
    val oEXAMATTEMPTID: Int,
    @SerializedName("EXAM_DURATION")
    val oEXAMDURATION: Long,
    @SerializedName("RESULT")
    val rESULT: String?
)
//{
//RESULT: "SUCCESS",
//EXAM_ATTEMPT_ID: 8,
//EXAM_DURATION: 30,
//ELAPSED_TIME: 0,
//JSON_ERROR: "NOERROR",
//}