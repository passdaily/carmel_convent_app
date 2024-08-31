package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class OnlineDExamResultModel(
    @SerializedName("DOnlineExamResult")
    val dOnlineExamResult: ArrayList<DOnlineExamResult>,
    @SerializedName("OnlineExamDetails")
    val onlineExamDetails: OnlineExamDetails
){
    @Keep
    data class DOnlineExamResult(
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ANSWER")
        val aNSWER: String?,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Any,
        @SerializedName("ANSWER_FILE_1")
        val aNSWERFILE1: String?,
        @SerializedName("ANSWER_FILE_2")
        val aNSWERFILE2: String?,
        @SerializedName("ANSWER_FILE_3")
        val aNSWERFILE3: String?,
        @SerializedName("ANSWER_MARK")
        val aNSWERMARK: Any,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String?,
        @SerializedName("ELAPSED_TIME")
        val eLAPSEDTIME: Any,
        @SerializedName("EXAM_ATTEMPT_D_ID")
        val eXAMATTEMPTDID: Any,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("EXAM_NAME")
        val eXAMNAME: String,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: Any,
        @SerializedName("QUESTION_CONTENT")
        val qUESTIONCONTENT: String,
        @SerializedName("QUESTION_ID")
        val qUESTIONID: Int,
        @SerializedName("QUESTION_ID_ATTEMPT")
        val qUESTIONIDATTEMPT: Any,
        @SerializedName("QUESTION_KEY")
        val qUESTIONKEY: Any,
        @SerializedName("QUESTION_MARK")
        val qUESTIONMARK: Int,
        @SerializedName("QUESTION_ORDER")
        val qUESTIONORDER: Int,
        @SerializedName("QUESTION_TITLE")
        val qUESTIONTITLE: String?,
        @SerializedName("QUESTION_TYPE_ID")
        val qUESTIONTYPEID: Int,
        @SerializedName("STATUS")
        val sTATUS: Int,

        @SerializedName("COMMENT_FILE")
        val aNSWERKEYFILE: String?,
        @SerializedName("COMMENT_TYPE")
        val aNSWERKEYFILETYPE: Int,
        @SerializedName("TEACHER_COMMENT")
        val aNSWERKEY: String?,


        @SerializedName("TOTAL_QUESTIONS")
        val tOTALQUESTIONS: Any
    )


    @Keep
    data class OnlineExamDetails(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Any,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("ELAPSED_TIME")
        val eLAPSEDTIME: Int,
        @SerializedName("END_TIME")
        val eNDTIME: Any,
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
        @SerializedName("TOTAL_MARK_SCORED")
        val tOTALMARKSCORED: Int,
        @SerializedName("TOTAL_OUTOFF_MARK")
        val tOTALOUTOFFMARK: Int,
        @SerializedName("TOTAL_QUESTIONS")
        val tOTALQUESTIONS: Int
    )
}