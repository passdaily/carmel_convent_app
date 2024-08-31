package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class QuestionListModel(
    @SerializedName("QuestionList")
    val questionList: ArrayList<Question>
){
    @Keep
    data class Question(
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ANSWERED_OPTION_ID")
        val aNSWEREDOPTIONID: Any,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Any,
        @SerializedName("ANSWER_KEY")
        val aNSWERKEY: String?,
        @SerializedName("ANSWER_KEY_FILE")
        val aNSWERKEYFILE: String,
        @SerializedName("ANSWER_KEY_FILE_TYPE")
        val aNSWERKEYFILETYPE: Int,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("ELAPSED_TIME")
        val eLAPSEDTIME: Any,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: Any,
        @SerializedName("OEXAM_ATTEMPT_D_ID")
        val oEXAMATTEMPTDID: Any,
        @SerializedName("OEXAM_ATTEMPT_ID")
        val oEXAMATTEMPTID: Any,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("QUESTION_CONTENT")
        val qUESTIONCONTENT: String,
        @SerializedName("QUESTION_ID")
        val qUESTIONID: Int,
        @SerializedName("QUESTION_ID_ATTEMPT")
        val qUESTIONIDATTEMPT: Any,
        @SerializedName("QUESTION_KEY")
        val qUESTIONKEY: Any,
        @SerializedName("QUESTION_ORDER")
        val qUESTIONORDER: Int,
        @SerializedName("QUESTION_TITLE")
        val qUESTIONTITLE: String,
        @SerializedName("QUESTION_TYPE_ID")
        val qUESTIONTYPEID: Int,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("TOTAL_QUESTIONS")
        val tOTALQUESTIONS: Any
    )
}