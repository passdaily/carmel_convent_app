package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PromoteStudentListModel(
    @SerializedName("StudentList")
    val studentList: List<Student>
){
    @Keep
    data class Student(
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("STUDENT_FATHER_NAME")
        val sTUDENTFATHERNAME: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
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

        var selectedValue : Boolean = false
    )
}