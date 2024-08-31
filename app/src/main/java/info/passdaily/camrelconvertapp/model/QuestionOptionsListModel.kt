package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class QuestionOptionsListModel(
    @SerializedName("OptionList")
    val optionList: List<Option>,
    @SerializedName("QuestionList")
    val questionList: List<Question>
){
    @Keep
    data class Option(
        @SerializedName("IS_RIGHT_OPTION")
        val iSRIGHTOPTION: Int,
        @SerializedName("OPTION_ID")
        val oPTIONID: Int,
        @SerializedName("OPTION_KEY")
        val oPTIONKEY: Any,
        @SerializedName("OPTION_TEXT")
        val oPTIONTEXT: String,
        @SerializedName("QUESTION_ID")
        val qUESTIONID: Int
    )
    @Keep
    data class Question(
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: Any,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("QUESTION_CONTENT")
        val qUESTIONCONTENT: String,
        @SerializedName("QUESTION_ID")
        val qUESTIONID: Int,
        @SerializedName("QUESTION_KEY")
        val qUESTIONKEY: Any,
        @SerializedName("QUESTION_ORDER")
        val qUESTIONORDER: Int,
        @SerializedName("QUESTION_TITLE")
        val qUESTIONTITLE: String,
        @SerializedName("QUESTION_TYPE_ID")
        val qUESTIONTYPEID: Int,
        @SerializedName("QUESTION_TYPE_NAME")
        val qUESTIONTYPENAME: Any,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,

        var optionList: ArrayList<Option>
    )
}