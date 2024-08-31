package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GuardianListModel(
    @SerializedName("StudentList")
    var guardianList: List<Student>
){
    @Keep
    data class Student(
        @SerializedName("ACCADEMIC_TIME")
        var aCCADEMICTIME: String,
        @SerializedName("ADMISSION_NUMBER")
        var aDMISSIONNUMBER: String?,
        @SerializedName("CLASS_NAME")
        var cLASSNAME: String,
        @SerializedName("STUDENT_FATHER_NAME")
        var sTUDENTFATHERNAME: String,
        @SerializedName("STUDENT_FNAME")
        var sTUDENTFNAME: String,
        @SerializedName("STUDENT_GENDER")
        var sTUDENTGENDER: Int,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        var sTUDENTGUARDIANNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NUMBER")
        var sTUDENTGUARDIANNUMBER: String,
        @SerializedName("STUDENT_ID")
        var sTUDENTID: Int,
        @SerializedName("STUDENT_LNAME")
        var sTUDENTLNAME: String,
        @SerializedName("STUDENT_MNAME")
        var sTUDENTMNAME: String,
        @SerializedName("STUDENT_PHONE_NUMBER")
        var sTUDENTPHONENUMBER: String,
        @SerializedName("STUDENT_ROLL_NUMBER")
        var sTUDENTROLLNUMBER: Int,
        @SerializedName("STUDENT_DOB")
        var sTUDENTDOB: String,

        var updated : Boolean = false
    )
}