package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProgressCardCbseModel(
    @SerializedName("MarkList")
    val markList: List<Mark>,
    @SerializedName("StaffList")
    val staffList: List<Staff>,
    @SerializedName("StudentList")
    val studentList: List<Student>,
    @SerializedName("SubjectList")
    val subjectList: List<Subject>
){
    @Keep
    data class Mark(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("MARK_DATE")
        val mARKDATE: String,
        @SerializedName("MARKED_BY")
        val mARKEDBY: Int,
        @SerializedName("MARK_GRADE")
        val mARKGRADE: String,
        @SerializedName("MARK_ID")
        val mARKID: Int,
        @SerializedName("OUTOFF_MARK")
        var oUTOFFMARK: Float,
        @SerializedName("PASS_MARK")
        var pASSMARK: Int,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("TOTAL_MARK")
        var tOTALMARK: Float,

        var sUBJECTNAME: String,
        var sUBJECTICON: String,
        var sUBJECTWISEPASS: Int,
        var tOTALATTEND: Int,
        var pASSSTATUS : Boolean,

        var pERCENTAGE : Int,
    )

    @Keep
    data class Staff(
        @SerializedName("STAFF_FNAME")
        val sTAFFFNAME: String,
        @SerializedName("STAFF_ID")
        val sTAFFID: Int,
        @SerializedName("STAFF_LNAME")
        val sTAFFLNAME: String,
        @SerializedName("STAFF_MNAME")
        val sTAFFMNAME: String,
        @SerializedName("STAFF_PHONE_NUMBER")
        val sTAFFPHONENUMBER: String
    )

    @Keep
    data class Student(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ADMISSION_NUMBER")
        val aDMISSIONNUMBER: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        val sTUDENTGUARDIANNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NUMBER")
        val sTUDENTGUARDIANNUMBER: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,

        var cLASSNAME : String,

        var markList : ArrayList<Mark>
    )

    @Keep
    data class Subject(
        @SerializedName("OUTOFF_MARK")
        val oUTOFFMARK: Int,
        @SerializedName("PASS_MARK")
        val pASSMARK: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String,
        @SerializedName("SUBJECTWISE_PASS")
        val sUBJECTWISEPASS: Int,
        @SerializedName("TOTAL_ATTEND")
        val tOTALATTEND: Int
    )
}