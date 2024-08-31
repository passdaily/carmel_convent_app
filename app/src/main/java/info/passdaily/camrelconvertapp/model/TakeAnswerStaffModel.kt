package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class TakeAnswerStaffModel(
    @SerializedName("Answer")
    var answer: Answer
) {
    @Keep
    data class Answer(
        @SerializedName("ANSWER")
        var aNSWER: String? = null,                                  //
        @SerializedName("ANSWER_FILE_1")                                  //"Answer": {
        var aNSWERFILE1: String? = null,                                    //"QUESTION_ID": 4,
        @SerializedName("ANSWER_FILE_2")                                  //"EXAM_ATTEMPT_D_ID": null,
        var aNSWERFILE2: String? = null,                                   //"QUESTION_ID_ATTEMPT": null,
        @SerializedName("ANSWER_FILE_3")                                  //"ANSWER": null,
        var aNSWERFILE3: String? = null,                                    //"ANSWER_FILE_1": null,
        @SerializedName("ANSWER_MARK")                                  //"ANSWER_FILE_2": null,
        var aNSWERMARK: Int,                                  //"ANSWER_FILE_3": null,
        @SerializedName("COMMENT_FILE")                                  //"ANSWER_MARK": -1,
        var cOMMENTFILE: String? = null,                                     //"TEACHER_COMMENT": null,
        @SerializedName("COMMENT_TYPE")                                  //"COMMENT_TYPE": null,
        var cOMMENTTYPE: Int?,                                  //"COMMENT_FILE": null
        @SerializedName("EXAM_ATTEMPT_D_ID")                                  //}
        var eXAMATTEMPTDID: Int?,                               //}
        @SerializedName("QUESTION_ID")                                  //
        var qUESTIONID: Int,                                  //
        @SerializedName("QUESTION_ID_ATTEMPT")
        var qUESTIONIDATTEMPT: Int?,
        @SerializedName("TEACHER_COMMENT")
        var tEACHERCOMMENT: String? = null,
    )
}