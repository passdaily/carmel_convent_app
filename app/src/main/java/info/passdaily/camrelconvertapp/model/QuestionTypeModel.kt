package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class QuestionTypeModel(
    @SerializedName("QuestionType")
    val questionType: List<QuestionType>
){
    @Keep
    data class QuestionType(
        @SerializedName("QUESTION_TYPE_ID")
        val qUESTIONTYPEID: Int,
        @SerializedName("QUESTION_TYPE_NAME")
        val qUESTIONTYPENAME: String
    )
}