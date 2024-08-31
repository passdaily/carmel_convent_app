package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DescGiveMarkPreviewModel(
    @SerializedName("OnlineExamDetails")
    var onlineExamDetails: OnlineExamDetails,
    @SerializedName("StudentQuestionAttemptedList")
    var studentQuestionAttemptedList: List<StudentQuestionAttempted>
) {
    @Keep
    data class OnlineExamDetails(                     //"EXAM_ATTEMPT_ID": 7,
        @SerializedName("ACCADEMIC_ID")                     //"ACCADEMIC_ID": 8,
        var aCCADEMICID: Int,                     //"CLASS_ID": 12,
        @SerializedName("ACCADEMIC_TIME")                     //"STUDENT_ID": 928,
        var aCCADEMICTIME: String,                     //"EXAM_ID": 1,
        @SerializedName("ADMIN_ID")                     //"START_TIME": "2022-06-20T16:00:06.45",
        var aDMINID: Any,                     //"END_TIME": "2022-06-20T16:03:14.57",
        @SerializedName("ANSWERED_QUESTIONS")                     //"ELAPSED_TIME": 0,
        var aNSWEREDQUESTIONS: Int,                     //"TOTAL_QUESTIONS": 10,
        @SerializedName("CLASS_ID")                     //"ANSWERED_QUESTIONS": 7,
        var cLASSID: Int,                     //"IS_PAUSED": 0,
        @SerializedName("CLASS_NAME")                     //"PAUSED_COUNT": 0,
        var cLASSNAME: String,                     //"IS_AUTO_ENDED": 0,
        @SerializedName("ELAPSED_TIME")                     //"IS_SUBMITTED": 0,
        var eLAPSEDTIME: Int,                     //"ADMIN_ID": 1,
        @SerializedName("END_TIME")                     //"EXAM_ATTEMPT_STATUS": 2,
        var eNDTIME: String?,                     //"EXAM_NAME": "Crop Production and Management",
        @SerializedName("EXAM_ATTEMPT_ID")                     //"EXAM_DESCRIPTION": "Unit Test Chapter Crop Production and Management ",
        var eXAMATTEMPTID: Int,                     //"EXAM_DURATION": 30,
        @SerializedName("EXAM_ATTEMPT_STATUS")                     //"CLASS_NAME": "VIII-A",
        var eXAMATTEMPTSTATUS: Int,                     //"SUBJECT_NAME": "Biology",
        @SerializedName("EXAM_DESCRIPTION")                     //"ACCADEMIC_TIME": "2021-2022",
        var eXAMDESCRIPTION: String,                     //"STUDENT_NAME": "THEERTHA V R"
        @SerializedName("EXAM_DURATION")
        var eXAMDURATION: Int,
        @SerializedName("EXAM_ID")
        var eXAMID: Int,
        @SerializedName("EXAM_NAME")
        var eXAMNAME: String,
        @SerializedName("IS_AUTO_ENDED")
        var iSAUTOENDED: Int,
        @SerializedName("IS_PAUSED")
        var iSPAUSED: Int,
        @SerializedName("IS_SUBMITTED")
        var iSSUBMITTED: Int,
        @SerializedName("PAUSED_COUNT")
        var pAUSEDCOUNT: Int,
        @SerializedName("START_TIME")
        var sTARTTIME: String,
        @SerializedName("STUDENT_ID")
        var sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        var sTUDENTNAME: String,
        @SerializedName("SUBJECT_NAME")
        var sUBJECTNAME: String,
        @SerializedName("TOTAL_QUESTIONS")
        var tOTALQUESTIONS: Int
    )

    @Keep
    data class StudentQuestionAttempted(
        @SerializedName("ADMIN_ID")
        var aDMINID: Int,
        @SerializedName("ANSWER")
        var aNSWER: String?,
        @SerializedName("ANSWER_FILE_1")
        var aNSWERFILE1: String?,
        @SerializedName("ANSWER_FILE_2")
        var aNSWERFILE2: String?,
        @SerializedName("ANSWER_FILE_3")
        var aNSWERFILE3: String?,
        @SerializedName("ANSWER_MARK")
        var aNSWERMARK: String?,
        @SerializedName("CREATED_DATE")
        var cREATEDDATE: String,
        @SerializedName("EXAM_ATTEMPT_D_ID")
        var eXAMATTEMPTDID: String?,
        @SerializedName("EXAM_ID")
        var eXAMID: Int,
        @SerializedName("EXAM_NAME")
        var eXAMNAME: String,
        @SerializedName("MODIFIED_DATE")
        var mODIFIEDDATE: String,
        @SerializedName("QUESTION_CONTENT")
        var qUESTIONCONTENT: String,
        @SerializedName("QUESTION_ID")
        var qUESTIONID: Int,
        @SerializedName("QUESTION_KEY")
        var qUESTIONKEY: Any,
        @SerializedName("QUESTION_MARK")
        var qUESTIONMARK: Int,
        @SerializedName("QUESTION_ORDER")
        var qUESTIONORDER: Int,
        @SerializedName("QUESTION_TITLE")
        var qUESTIONTITLE: String?,
        @SerializedName("QUESTION_TYPE_ID")
        var qUESTIONTYPEID: Int,
        @SerializedName("STATUS")
        var sTATUS: Int
    )
}