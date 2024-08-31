package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ResultListModel(
    @SerializedName("Result")
    val result: List<Result>
){
    @Keep
    data class Result(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("RESULT_STATUS")
        var rESULTSTATUS: Int,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        var rESULTSTRING : String = "",
        var isChecked : Boolean = false
    )


    data class ResultCheck(
        val sTUDENTID: Int,
        val sTUDENTROLLNUMBER: Int,
        var isCheckedValue : Int = -1
    )
}