package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MarkRegisterMspsLpUpModel(
    @SerializedName("Marks")
    val marksList: List<Mark>
){
    @Keep
    data class Mark(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("MARK_GRADE")
        val mARKGRADE: String,
        @SerializedName("MARK_ID")
        val mARKID: Int,
        @SerializedName("OUTOFF_MARK")
        val oUTOFFMARK: String,
        @SerializedName("OUTOFF_MARK_CE")
        val oUTOFFMARKCE: String,
        @SerializedName("PASS_MARK")
        val pASSMARK: String,
        @SerializedName("PASS_MARK_CE")
        val pASSMARKCE: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("TOTAL_MARK")
        var tOTALMARK: String,
        @SerializedName("TOTAL_MARK_CE_1")
        var tOTALMARKCE1: String,
        @SerializedName("TOTAL_MARK_CE_2")
        var tOTALMARKCE2: String,
        @SerializedName("TOTAL_MARK_CE_3")
        var tOTALMARKCE3: String,
        @SerializedName("TOTAL_MARK_CE_4")
        var tOTALMARKCE4: String,

        var updated : Boolean = false
    )
}