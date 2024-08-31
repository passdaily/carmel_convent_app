package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AssignmentDetailsGetByIdModel(
    @SerializedName("AssignmentDetails")
    val assignmentDetails: AssignmentDetails,
    @SerializedName("AssignmentFileList")
    val assignmentFileList: List<AssignmentFile>
){
    @Keep
    data class AssignmentDetails(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: Any,
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
        val cLASSNAME: Any,
        @SerializedName("CREATED_BY")
        val cREATEDBY: Any,
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
        val sUBJECTNAME: Any,
        @SerializedName("TOTAL_STUDENT")
        val tOTALSTUDENT: Any,
        @SerializedName("TOTAL_SUBMIT")
        val tOTALSUBMIT: Any
    )

    @Keep
    data class AssignmentFile(
        @SerializedName("ASSIGNMENT_FILE")
        val aSSIGNMENTFILE: String,
        @SerializedName("ASSIGNMENT_FILE_ID")
        val aSSIGNMENTFILEID: Int,
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int
    )
}