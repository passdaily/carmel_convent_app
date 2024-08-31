package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class OptionListStaffModel(
    @SerializedName("OptionList")
    val optionList: List<Option>
){
    @Keep
    data class Option(
        @SerializedName("IS_RIGHT_OPTION")
        var iSRIGHTOPTION: Int,
        @SerializedName("OPTION_ID")
        var oPTIONID: Int,
        @SerializedName("OPTION_KEY")
        var oPTIONKEY: String,
        @SerializedName("OPTION_TEXT")
        var oPTIONTEXT: String,
        @SerializedName("QUESTION_ID")
        var qUESTIONID: Int,

        var isNewCheck  : Boolean = false
    )
}