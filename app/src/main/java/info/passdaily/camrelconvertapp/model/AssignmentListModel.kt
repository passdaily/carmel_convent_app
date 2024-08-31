package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AssignmentListModel(
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
        @SerializedName("ASSIGNMENT_ID")
        val aSSIGNMENTID: Int,
        @SerializedName("ASSIGNMENT_MARK")
        val aSSIGNMENTMARK: Any,
        @SerializedName("ASSIGNMENT_NAME")
        val aSSIGNMENTNAME: String,
        @SerializedName("ASSIGNMENT_STUTUS")
        val aSSIGNMENTSTUTUS: Int,
        @SerializedName("ASSIGNMENT_SUBMIT_ID")
        val aSSIGNMENTSUBMITID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("END_DATE")
        val eNDDATE: String,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: Any,
        @SerializedName("START_DATE")
        val sTARTDATE: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TIME_NOW")
        val tIMENOW: String,
        //CHAPTER_NO
        //PAGE_NO
        @SerializedName("CHAPTER_NO")
        val cHAPTERNO: String,
        @SerializedName("PAGE_NO")
        val pAGENO: String,
    )
}