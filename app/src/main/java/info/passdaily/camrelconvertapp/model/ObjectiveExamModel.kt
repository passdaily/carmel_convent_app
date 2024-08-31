package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ObjectiveExamModel(
    @SerializedName("OnlineExamList")
    val onlineExamList: List<OnlineExam>
){
    @Keep
    data class OnlineExam(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ALLOWED_PAUSE")
        val aLLOWEDPAUSE: Int,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Any,
        @SerializedName("ATTEMPTED_ON")
        val aTTEMPTEDON: Any,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("IS_AUTO_ENDED")
        val iSAUTOENDED: Int,
        @SerializedName("IS_SUBMITTED")
        val iSSUBMITTED: Int,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: Any,
        @SerializedName("OEXAM_ATTEMPT_ID")
        val oEXAMATTEMPTID: Int,
        @SerializedName("OEXAM_DESCRIPTION")
        val oEXAMDESCRIPTION: String,
        @SerializedName("OEXAM_DURATION")
        val oEXAMDURATION: Int,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_KEY")
        val oEXAMKEY: Any,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("PAUSED_COUNT")
        val pAUSEDCOUNT: Any,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: Any,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TIME_NOW")
        val tIMENOW: String,
        @SerializedName("TOTAL_QUESTION")
        val tOTALQUESTION: Int
    )

}