package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GroupStudentModel(
    @SerializedName("GroupStudents")
    val groupStudents: List<GroupStudent>
){
    @Keep
    data class GroupStudent(
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("GMEMBER_ID")
        val gMEMBERID: Int,
        @SerializedName("GROUP_NAME")
        val gROUPNAME: String,
        @SerializedName("STUDENT_FATHER_NAME")
        val sTUDENTFATHERNAME: Any,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        val sTUDENTGUARDIANNAME: Any,
        @SerializedName("STUDENT_GUARDIAN_NUMBER")
        val sTUDENTGUARDIANNUMBER: Any,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_LNAME")
        val sTUDENTLNAME: String,
        @SerializedName("STUDENT_MNAME")
        val sTUDENTMNAME: String,
        @SerializedName("STUDENT_PHONE_NUMBER")
        val sTUDENTPHONENUMBER: String,

        var isChecked : Boolean = false
    )
}