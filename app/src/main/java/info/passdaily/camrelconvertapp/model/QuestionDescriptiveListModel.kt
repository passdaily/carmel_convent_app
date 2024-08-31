package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class QuestionDescriptiveListModel(
    @SerializedName("QuestionList")
    val questionList: List<Question>
){
    @Keep
    data class Question(
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_BY")
        val cREATEDBY: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
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
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String
    )
}