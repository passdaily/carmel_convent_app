package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StudentSubmissionDetailsModel(
    @SerializedName("StudentAttachmentList")
    val studentAttachmentList: List<StudentAttachment>,
    @SerializedName("StudentSubmittedDetails")
    var studentSubmittedDetails: StudentSubmittedDetails
){

    @Keep
    data class StudentSubmittedDetails(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ASSIGNMENT_DESCRIPTION")
        val aSSIGNMENTDESCRIPTION: String,
        @SerializedName("ASSIGNMENT_DETAILS")
        val aSSIGNMENTDETAILS: String,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int,
        @SerializedName("ASSIGNMENT_MARK")
        val aSSIGNMENTMARK: Int,
        @SerializedName("ASSIGNMENT_NAME")
        val aSSIGNMENTNAME: String,
        @SerializedName("ASSIGNMENT_OUTOFF_MARK")
        val aSSIGNMENTOUTOFFMARK: Int,
        @SerializedName("ASSIGNMENT_REPLY")
        val aSSIGNMENTREPLY: String?,
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
        @SerializedName("CLASS_SECTION")
        val cLASSSECTION: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String
    )

    @Keep
    data class StudentAttachment(
        @SerializedName("ASSIGNMENT_FILE")
        val aSSIGNMENTFILE: String,
        @SerializedName("ASSIGNMENT_FILE_ID")
        val aSSIGNMENTFILEID: Int,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Any
    )
}