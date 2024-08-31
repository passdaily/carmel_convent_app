package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AssignmentDetailsStaffModel(
    @SerializedName("AssignmentDetails")
    val assignmentDetails: AssignmentDetails,
    @SerializedName("StaffAttachmentList")
    val staffAttachmentList: List<StaffAttachment>,
    @SerializedName("SubmittedDetails")
    val submittedDetails: List<SubmittedDetail>
){

    @Keep
    data class AssignmentDetails(
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
        @SerializedName("TOTAL_STUDENT")
        val tOTALSTUDENT: Any,
        @SerializedName("TOTAL_SUBMIT")
        val tOTALSUBMIT: Any
    )

    @Keep
    data class StaffAttachment(
        @SerializedName("ASSIGNMENT_FILE")
        val aSSIGNMENTFILE: String,
        @SerializedName("ASSIGNMENT_FILE_ID")
        val aSSIGNMENTFILEID: Int,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int
    )


    @Keep
    data class SubmittedDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ASSIGNMENT_DETAILS")
        val aSSIGNMENTDETAILS: String,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int,
        @SerializedName("ASSIGNMENT_MARK")
        val aSSIGNMENTMARK: Int,
        @SerializedName("ASSIGNMENT_OUTOFF_MARK")
        val aSSIGNMENTOUTOFFMARK: Int,
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
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_FILES")
        val tOTALFILES: Int
    )
}