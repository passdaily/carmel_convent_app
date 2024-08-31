package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ReallocationStudentListModel(
    @SerializedName("StudentList")
    var studentList: List<Student>
){
    @Keep
    data class Student(
        @SerializedName("ACCADEMIC_TIME")
        var aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        var cLASSID: Int,
        @SerializedName("CLASS_NAME")
        var cLASSNAME: String,
        @SerializedName("STUD_ACCADEMIC_ID")
        var sTUDACCADEMICID: Int,
        @SerializedName("STUDENT_FNAME")
        var sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        var sTUDENTID: Int,
        @SerializedName("STUDENT_LNAME")
        var sTUDENTLNAME: String,
        @SerializedName("STUDENT_MNAME")
        var sTUDENTMNAME: String,
        @SerializedName("STUDENT_ROLL_NUMBER")
        var sTUDENTROLLNUMBER: Int,

        var updated : Boolean = false
    )
}