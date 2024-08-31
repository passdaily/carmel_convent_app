package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProgressCardLpUpModel(
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
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,

        var cLASSNAME : String,

        var markList : ArrayList<Mark>

    )

    @Keep
    data class Subject(
        @SerializedName("OUTOFF_MARK_INTERNAL")
        val oUTOFFMARKINTERNAL: Int,
        @SerializedName("OUTOFF_MARK_THEORY")
        val oUTOFFMARKTHEORY: Int,
        @SerializedName("PASS_MARK_INTERNAL")
        val pASSMARKINTERNAL: Int,
        @SerializedName("PASS_MARK_THEORY")
        val pASSMARKTHEORY: Int,
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

    @Keep
    data class Mark(
        @SerializedName("ACCADEMIC_ID")////////////////////////////// {
        val aCCADEMICID: Int,//////////////////////////////      "MARK_ID": 1226,
        @SerializedName("CLASS_ID")//////////////////////////////      "ACCADEMIC_ID": 8,
        val cLASSID: Int,//////////////////////////////      "CLASS_ID": 17,
        @SerializedName("EXAM_ID")//////////////////////////////      "EXAM_ID": 29,
        val eXAMID: Int,//////////////////////////////      "SUBJECT_ID": 8,
        @SerializedName("MARK_DATE")//////////////////////////////      "STUDENT_ID": 351,
        val mARKDATE: String,//////////////////////////////      "STUDENT_ROLL_NUMBER": 1,
        @SerializedName("MARKED_BY")//////////////////////////////      "PASS_MARK_THEORY": 0,
        val mARKEDBY: Int,//////////////////////////////      "OUTOFF_MARK_THEORY": 0,
        @SerializedName("MARK_ID")//////////////////////////////      "TOTAL_MARK_THEORY": 0,
        val mARKID: Int,//////////////////////////////      "PASS_MARK_PRACTICAL": null,
        @SerializedName("OUTOFF_MARK_INTERNAL")//////////////////////////////      "OUTOFF_MARK_PRACTICAL": null,
        var oUTOFFMARKINTERNAL: Int,//////////////////////////////      "TOTAL_MARK_PRACTICAL": null,
        @SerializedName("OUTOFF_MARK_PRACTICAL")//////////////////////////////      "TOTAL_GRADE": "",
        var oUTOFFMARKPRACTICAL: Any,//////////////////////////////      "PASS_MARK_INTERNAL": 0,
        @SerializedName("OUTOFF_MARK_THEORY")//////////////////////////////      "OUTOFF_MARK_INTERNAL": 0,
        val oUTOFFMARKTHEORY: Int,//////////////////////////////      "TOTAL_MARK_INTERNAL": 0,
        @SerializedName("PASS_MARK_INTERNAL")//////////////////////////////      "MARK_DATE": "2022-10-31T19:31:27.963",
        var pASSMARKINTERNAL: Int,//////////////////////////////      "MARKED_BY": 1,
        @SerializedName("PASS_MARK_PRACTICAL")//////////////////////////////      "GRADE_TE": "B",
        var pASSMARKPRACTICAL: Any,//////////////////////////////      "GRADE_CE": "C"
        @SerializedName("PASS_MARK_THEORY")//////////////////////////////    },
        var pASSMARKTHEORY: Int,//////////////////////////////
        @SerializedName("STUDENT_ID")//////////////////////////////
        val sTUDENTID: Int,//////////////////////////////
        @SerializedName("STUDENT_ROLL_NUMBER")//////////////////////////////
        val sTUDENTROLLNUMBER: Int,//////////////////////////////
        @SerializedName("SUBJECT_ID")//////////////////////////////
        val sUBJECTID: Int,//////////////////////////////
        @SerializedName("TOTAL_GRADE")//////////////////////////////
        val tOTALGRADE: String,//////////////////////////////
        @SerializedName("TOTAL_MARK_INTERNAL")//////////////////////////////
        var tOTALMARKINTERNAL: Int,//////////////////////////////
        @SerializedName("TOTAL_MARK_PRACTICAL")//////////////////////////////
        var tOTALMARKPRACTICAL: Any,//////////////////////////////
        @SerializedName("TOTAL_MARK_THEORY")//////////////////////////////
        var tOTALMARKTHEORY: Int,
        @SerializedName("GRADE_TE")//////////////////////////////
        val gRADETE: String?,
        @SerializedName("GRADE_CE")//////////////////////////////
        val gRADECE: String?, ////////////////////////////////////////////////////////////

        var sUBJECTNAME: String,
        var sUBJECTICON: String,
        var sUBJECTWISEPASS: Int,
        var tOTALATTEND: Int,
        var pASSSTATUS : Boolean,

        var totalMark: Int,

        var outOffMarkTotal : Int,
        var pERCENTAGE : Int,

//
//
//        val sUBJECTCODE: String,
//        val sUBJECTNAME: String,
//        val sUBJECTWISEPASS: Int,
//        val tOTALATTEND: Int

    )
}