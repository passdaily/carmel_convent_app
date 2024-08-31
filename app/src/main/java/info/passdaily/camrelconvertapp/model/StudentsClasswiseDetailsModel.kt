package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StudentsClasswiseDetailsModel(
    @SerializedName("Students")
    val students: List<Student>
){
    @Keep
    data class Student(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("STUDENT_CADDRESS")
        val sTUDENTCADDRESS: String,
        @SerializedName("STUDENT_DOB")
        val sTUDENTDOB: String?,
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
        @SerializedName("STUDENT_MOTHER_NAME")
        val sTUDENTMOTHERNAME: String,
        @SerializedName("STUDENT_PHONE_NUMBER")
        val sTUDENTPHONENUMBER: String,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int
    )
}