package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class QuestionDListModel(
    @SerializedName("QuestionList")
    val questionList: ArrayList<Question>
){
    @Keep
    data class Question(
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ANSWER")
        val aNSWER: Any,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Int,
        @SerializedName("ANSWER_FILE_1")
        val aNSWERFILE1: Any,
        @SerializedName("ANSWER_FILE_2")
        val aNSWERFILE2: Any,
        @SerializedName("ANSWER_FILE_3")
        val aNSWERFILE3: Any,
        @SerializedName("ANSWER_MARK")
        val aNSWERMARK: Any,
        @SerializedName("COMMENT_FILE")
        val cOMMENTFILE: Any,
        @SerializedName("COMMENT_TYPE")
        val cOMMENTTYPE: Any,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("ELAPSED_TIME")
        val eLAPSEDTIME: Int,
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
        val qUESTIONTITLE: String,
        @SerializedName("QUESTION_TYPE_ID")
        val qUESTIONTYPEID: Int,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("TEACHER_COMMENT")
        val tEACHERCOMMENT: Any,
        @SerializedName("TOTAL_QUESTIONS")
        val tOTALQUESTIONS: Int
    )
}