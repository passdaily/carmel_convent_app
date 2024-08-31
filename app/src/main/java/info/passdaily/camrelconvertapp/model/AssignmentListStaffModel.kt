package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AssignmentListStaffModel(
    @SerializedName("AssignmentList")
    val assignmentList: List<Assignment>
){
    @Keep
    data class Assignment(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ASSIGNMENT_DESCRIPTION")
        val aSSIGNMENTDESCRIPTION: String,
        @SerializedName("ASSIGNMENT_FILE")
        val aSSIGNMENTFILE: Any,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int,
        @SerializedName("ASSIGNMENT_NAME")
        val aSSIGNMENTNAME: String,
        @SerializedName("ASSIGNMENT_OUTOFF_MARK")
        val aSSIGNMENTOUTOFFMARK: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_BY")
        val cREATEDBY: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("END_DATE")
        val eNDDATE: String,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: String,
        @SerializedName("START_DATE")
        val sTARTDATE: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String?,
        @SerializedName("TOTAL_STUDENT")
        val tOTALSTUDENT: Int,
        @SerializedName("TOTAL_SUBMIT")
        val tOTALSUBMIT: Int,
        @SerializedName("CHAPTER_NO")
        val cHAPTERNO: String?,
        @SerializedName("PAGE_NO")
        val pAGENO: String?

        //"CHAPTER_NO": "1,2,3,4,5,8,9,",
        //"PAGE_NO": "1,2,4,7,8,9,10,"
    )
}