package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class FeesDetailsModel(
    @SerializedName("FeesPaidDetails")
    val feesPaidDetails: ArrayList<FeesPaidDetail>
){
    @Keep
    data class FeesPaidDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("RECEIPT_DATE")
        val rECEIPTDATE: String,
        @SerializedName("RECEIPT_ID")
        val rECEIPTID: Int,
        @SerializedName("RECEIPT_TOTAL")
        val rECEIPTTOTAL: Int,
        @SerializedName("ROLL_NUMBER")
        val rOLLNUMBER: Int,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int
    )
}