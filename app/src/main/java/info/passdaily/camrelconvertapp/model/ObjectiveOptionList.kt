package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ObjectiveOptionList(
    @SerializedName("AttemptedOption")
    val attemptedOption: ArrayList<AttemptedOption>,
    @SerializedName("OptionList")
    val optionList: ArrayList<Option>
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
    data class AttemptedOption(
        @SerializedName("ANSWERED_OPTION_ID")
        val aNSWEREDOPTIONID: Int,
        @SerializedName("CORRECT_OPTION_ID")
        val cORRECTOPTIONID: Int,
        @SerializedName("IS_CORRECT")
        val iSCORRECT: Int,
        @SerializedName("OEXAM_ATTEMPT_D_ID")
        val oEXAMATTEMPTDID: Int,
        @SerializedName("OEXAM_ATTEMPT_ID")
        val oEXAMATTEMPTID: Int,
        @SerializedName("QUESTION_ID")
        val qUESTIONID: Int
    )
}