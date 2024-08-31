package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProgressCardKgToXiiModel(
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
    data class Mark(                       //
        @SerializedName("ACCADEMIC_ID")                       //MARK_ID: 801,
        val aCCADEMICID: Int,                       //ACCADEMIC_ID: 8,
        @SerializedName("CLASS_ID")                       //CLASS_ID: 1,
        val cLASSID: Int,                       //EXAM_ID: 1,
        @SerializedName("EXAM_ID")                       //SUBJECT_ID: 9,
        val eXAMID: Int,                       //STUDENT_ID: 534,
        @SerializedName("MARK_DATE")                       //STUDENT_ROLL_NUMBER: 2,
        val mARKDATE: String,                       //PASS_MARK: 33,
        @SerializedName("MARKED_BY")                       //OUTOFF_MARK: 100,
        val mARKEDBY: Int,                       //TOTAL_MARK: 85,
        @SerializedName("MARK_GRADE")                       //MARK_DATE: "2022-05-31T13:16:10.253",
        val mARKGRADE: String,                       //MARKED_BY: 2,
        @SerializedName("MARK_ID")                       //MARK_GRADE: "A",
        val mARKID: Int,                       //PASS_MARK_CE: null,
        @SerializedName("OUTOFF_MARK")                       //OUTOFF_MARK_CE: null,
        val oUTOFFMARK: Int,                       //TOTAL_MARK_CE: null,
        @SerializedName("OUTOFF_MARK_CE")                       //
        val oUTOFFMARKCE: Any,                       //
        @SerializedName("PASS_MARK")                       //
        val pASSMARK: Int,
        @SerializedName("PASS_MARK_CE")
        val pASSMARKCE: Any,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("TOTAL_MARK")
        var tOTALMARK: Int,
        @SerializedName("TOTAL_MARK_CE")
        val tOTALMARKCE: Any,

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