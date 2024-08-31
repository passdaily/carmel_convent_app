package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class TakeAnswerModel(
    @SerializedName("Answer")
    val answer: Answer
){
    @Keep
    data class Answer(
        @SerializedName("ANSWER")
        val aNSWER: String?,
        @SerializedName("ANSWER_FILE_1")
        val aNSWERFILE1: String?,
        @SerializedName("ANSWER_FILE_2")
        val aNSWERFILE2: String?,
        @SerializedName("ANSWER_FILE_3")
        val aNSWERFILE3: String?,
        @SerializedName("EXAM_ATTEMPT_D_ID")
        val eXAMATTEMPTDID: Int,
        @SerializedName("QUESTION_ID")
        val qUESTIONID: Int,
        @SerializedName("QUESTION_ID_ATTEMPT")
        val qUESTIONIDATTEMPT: Int
    )
}