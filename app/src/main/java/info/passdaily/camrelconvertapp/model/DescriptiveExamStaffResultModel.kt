package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DescriptiveExamStaffResultModel(
    @SerializedName("OnlineExamAttendees")
    val onlineExamAttendees: List<OnlineExamAttendee>,
    @SerializedName("OnlineExamDetails")
    val onlineExamDetails: List<OnlineExamDetail>
){
    @Keep
    data class OnlineExamDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ALLOWED_PAUSE")
        val aLLOWEDPAUSE: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_BY")
        val cREATEDBY: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("EXAM_DESCRIPTION")
        val eXAMDESCRIPTION: String,
        @SerializedName("EXAM_DURATION")
        val eXAMDURATION: Int,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("EXAM_KEY")
        val eXAMKEY: Any,
        @SerializedName("EXAM_NAME")
        val eXAMNAME: String,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: String,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_QUESTION")
        val tOTALQUESTION: Int,
        @SerializedName("TOTAL_QUESTION_MARK")
        val tOTALQUESTIONMARK: Int
    )

    @Keep
    data class OnlineExamAttendee(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("ELAPSED_TIME")
        val eLAPSEDTIME: Int,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("EXAM_ATTEMPT_ID")
        val eXAMATTEMPTID: Int,
        @SerializedName("EXAM_ATTEMPT_STATUS")
        val eXAMATTEMPTSTATUS: Int,
        @SerializedName("EXAM_DESCRIPTION")
        val eXAMDESCRIPTION: String,
        @SerializedName("EXAM_DURATION")
        val eXAMDURATION: Int,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("EXAM_NAME")
        val eXAMNAME: String,
        @SerializedName("IS_AUTO_ENDED")
        val iSAUTOENDED: Int,
        @SerializedName("IS_PAUSED")
        val iSPAUSED: Int,
        @SerializedName("IS_SUBMITTED")
        val iSSUBMITTED: Int,
        @SerializedName("PAUSED_COUNT")
        val pAUSEDCOUNT: Int,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_ANSWER_MARK")
        val tOTALANSWERMARK: Int,
        @SerializedName("TOTAL_QUESTION_MARK")
        val tOTALQUESTIONMARK: Int,
        @SerializedName("TOTAL_QUESTIONS")
        val tOTALQUESTIONS: Int
    )
}