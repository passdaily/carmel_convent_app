package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProgressCardMspHsModel(
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
        @SerializedName("ACCADEMIC_ID")                          /////MARK_ID: 1,
        val aCCADEMICID: Int,                          /////ACCADEMIC_ID: 8,
        @SerializedName("CLASS_ID")                          /////CLASS_ID: 12,
        val cLASSID: Int,                          /////EXAM_ID: 1,
        @SerializedName("EXAM_ID")                          /////SUBJECT_ID: 9,
        val eXAMID: Int,                          /////STUDENT_ID: 901,
        @SerializedName("MARK_DATE")                          /////STUDENT_ROLL_NUMBER: 1,
        val mARKDATE: String,                          /////PASS_MARK: 25,
        @SerializedName("MARKED_BY")                          /////OUTOFF_MARK: 80,
        val mARKEDBY: Int,                          /////TOTAL_MARK: 45,
        @SerializedName("MARK_GRADE")                          /////PASS_MARK_CE: 8,
        val mARKGRADE: String,                          /////OUTOFF_MARK_CE: 20,
        @SerializedName("MARK_ID")                          /////TOTAL_MARK_CE_1: 8.7,
        val mARKID: Int,                          /////TOTAL_MARK_CE_2: 8.3,
        @SerializedName("OUTOFF_MARK")                          /////TOTAL_MARK_CE_3: 7.3,
        var oUTOFFMARK: Float,                          /////TOTAL_MARK_CE_4: 7.6,
        @SerializedName("OUTOFF_MARK_CE")                          /////MARK_GRADE: "B1",
        var oUTOFFMARKCE: Float,                          /////MARK_DATE: "2022-08-25T23:28:06.873",
        @SerializedName("PASS_MARK")                          /////MARKED_BY: 1,
        var pASSMARK: Float,                          /////
        @SerializedName("PASS_MARK_CE")
        var pASSMARKCE: Float,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("SUBJECT_ID")
        var sUBJECTID: Int,
        @SerializedName("TOTAL_MARK")
        var tOTALMARK: Float,
        @SerializedName("TOTAL_MARK_CE_1")
        var tOTALMARKCE1: Float,
        @SerializedName("TOTAL_MARK_CE_2")
        var tOTALMARKCE2: Float,
        @SerializedName("TOTAL_MARK_CE_3")
        var tOTALMARKCE3: Float,
        @SerializedName("TOTAL_MARK_CE_4")
        var tOTALMARKCE4: Float,

        var sUBJECTNAME: String,
        var sUBJECTICON: String,
        var sUBJECTWISEPASS: Int,
        var tOTALATTEND: Int,
        var pASSSTATUS : Boolean,


        var totalMark: Float,

        var outOffMarkTotal : Float,
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
        val sTAFFPHONENUMBER: Any
    )

    @Keep
    data class Student(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ADMISSION_NUMBER")
        val aDMISSIONNUMBER: Int,
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
        @SerializedName("OUTOFF_MARK_CE")
        val oUTOFFMARKCE: Int,
        @SerializedName("PASS_MARK")
        val pASSMARK: Int,
        @SerializedName("PASS_MARK_CE")
        val pASSMARKCE: Int,
        @SerializedName("SUBJECT_CODE")
        val sUBJECTCODE: String,
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