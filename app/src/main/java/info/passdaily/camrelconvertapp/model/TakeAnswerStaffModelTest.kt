package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class TakeAnswerStaffModelTest(
    @SerializedName("Answer")
    var answer: Answer
) {
    @Keep
    data class Answer(
        @SerializedName("ANSWER")
        var aNSWER: Any,
        @SerializedName("ANSWER_FILE_1")
        var aNSWERFILE1: Any,
        @SerializedName("ANSWER_FILE_2")
        var aNSWERFILE2: Any,
        @SerializedName("ANSWER_FILE_3")
        var aNSWERFILE3: Any,
        @SerializedName("ANSWER_MARK")
        var aNSWERMARK: Int,
        @SerializedName("COMMENT_FILE")
        var cOMMENTFILE: Any,
        @SerializedName("COMMENT_TYPE")
        var cOMMENTTYPE: Any,
        @SerializedName("EXAM_ATTEMPT_D_ID")
        var eXAMATTEMPTDID: Any,
        @SerializedName("QUESTION_ID")
        var qUESTIONID: Int,
        @SerializedName("QUESTION_ID_ATTEMPT")
        var qUESTIONIDATTEMPT: Any,
        @SerializedName("TEACHER_COMMENT")
        var tEACHERCOMMENT: Any
    )
}