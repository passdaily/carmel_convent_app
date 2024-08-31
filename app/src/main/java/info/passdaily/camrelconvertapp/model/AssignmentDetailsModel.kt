package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AssignmentDetailsModel(
    @SerializedName("AssignmentDetails")
    val assignmentDetails: AssignmentDetails,
    @SerializedName("AssignmentAttachmentList")
    val assignmentAttachmentList: ArrayList<AssignmentAttachment>,
    @SerializedName("AssignmentStudentStatus")
    val assignmentStudentStatus: AssignmentStudentStatus,
    @SerializedName("AssignmentFileList")
    val assignmentFileList: ArrayList<AssignmentFile>

){
    @Keep
    data class AssignmentDetails(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ASSIGNMENT_DESCRIPTION")
        val aSSIGNMENTDESCRIPTION: String,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int,
        @SerializedName("ASSIGNMENT_NAME")
        val aSSIGNMENTNAME: String,
        @SerializedName("ASSIGNMENT_OUTOFF_MARK")
        val aSSIGNMENTOUTOFFMARK: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
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
        @SerializedName("TIME_NOW")
        val tIMENOW: String,
        //CHAPTER_NO
        //PAGE_NO
        @SerializedName("CHAPTER_NO")
        val cHAPTERNO: String,
        @SerializedName("PAGE_NO")
        val pAGENO: String,
    )

    @Keep
    data class AssignmentFile(
        @SerializedName("ASSIGNMENT_FILE")
        val aSSIGNMENTFILE: String,
        @SerializedName("ASSIGNMENT_FILE_ID")
        val aSSIGNMENTFILEID: Int,
        @SerializedName("ASSIGNMENT_SUBMIT_ID")
        val aSSIGNMENTSUBMITID: Int
    )

    @Keep
    data class AssignmentStudentStatus(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ASSIGNMENT_DETAILS")
        val aSSIGNMENTDETAILS: String,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int,
        @SerializedName("ASSIGNMENT_MARK")
        val aSSIGNMENTMARK: Int,
        @SerializedName("ASSIGNMENT_REPLY")
        val aSSIGNMENTREPLY: String,
        @SerializedName("ASSIGNMENT_STUTUS")
        val aSSIGNMENTSTUTUS: Int,
        @SerializedName("ASSIGNMENT_SUBMIT_DATE")
        val aSSIGNMENTSUBMITDATE: String,
        @SerializedName("ASSIGNMENT_SUBMIT_ID")
        val aSSIGNMENTSUBMITID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int
    )

    @Keep
    data class AssignmentAttachment(
        @SerializedName("ASSIGNMENT_FILE")
        val aSSIGNMENTFILE: String,
        @SerializedName("ASSIGNMENT_FILE_ID")
        val aSSIGNMENTFILEID: Int,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int
    )
}