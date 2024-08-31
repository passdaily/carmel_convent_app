package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StudentRemarksModel(
    @SerializedName("RemarksList")
    val remarksList: List<Remarks>
){
    @Keep
    data class Remarks(
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMISSION_NUMBER")
        val aDMISSIONNUMBER: String,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("STUDENT_EXTRA")
        var sTUDENTEXTRA: String?,
        @SerializedName("STUDENT_FATHER_NAME")
        val sTUDENTFATHERNAME: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_GENDER")
        val sTUDENTGENDER: Int,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        val sTUDENTGUARDIANNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NUMBER")
        val sTUDENTGUARDIANNUMBER: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_LNAME")
        val sTUDENTLNAME: String,
        @SerializedName("STUDENT_MNAME")
        val sTUDENTMNAME: String,
        @SerializedName("STUDENT_PHONE_NUMBER")
        val sTUDENTPHONENUMBER: String,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,

        var update : Boolean = false
    )
}