package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AboutusFaqListModel(
    @SerializedName("AboutusList")
    val aboutusList: List<Aboutus>
){
    @Keep
    data class Aboutus(
        @SerializedName("ABT_FAQ_CREATEDBY")
        val aBTFAQCREATEDBY: Any,
        @SerializedName("ABT_FAQ_CREATEDBY_ID")
        val aBTFAQCREATEDBYID: Int,
        @SerializedName("ABT_FAQ_DATE")
        val aBTFAQDATE: String,
        @SerializedName("ABT_FAQ_DESCRIPTION")
        val aBTFAQDESCRIPTION: String,
        @SerializedName("ABT_FAQ_ID")
        val aBTFAQID: Int,
        @SerializedName("ABT_FAQ_STATUS")
        val aBTFAQSTATUS: Int,
        @SerializedName("ABT_FAQ_TITLE")
        val aBTFAQTITLE: String,
        @SerializedName("ABT_FAQ_TYPE")
        val aBTFAQTYPE: Int
    )
}