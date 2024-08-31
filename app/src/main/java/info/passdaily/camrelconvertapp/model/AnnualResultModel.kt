package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AnnualResultModel(
    @SerializedName("AnnualResult")
    val annualResult: ArrayList<AnnualResult>
){
    @Keep
    data class AnnualResult(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_YEAR")
        val aCCADEMICYEAR: String,
        @SerializedName("ADMISSION_NUMBER")
        val aDMISSIONNUMBER: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("RESULT_DESCRIPTION")
        val rESULTDESCRIPTION: String,
        @SerializedName("RESULT_ID")
        val rESULTID: Int,
        @SerializedName("RESULT_REMARK")
        val rESULTREMARK: String,
        @SerializedName("RESULT_STATUS")
        val rESULTSTATUS: Int,
        @SerializedName("RESULT_STATUS_NAME")
        val rESULTSTATUSNAME: String,
        @SerializedName("ROLL_NUMBER")
        val rOLLNUMBER: Int,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("STUDENT_GENDER")
        val sTUDENTGENDER: Int,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_IMAGE")
        val sTUDENTIMAGE: String,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String
    )
}