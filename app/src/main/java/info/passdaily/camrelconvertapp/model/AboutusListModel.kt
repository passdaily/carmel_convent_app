package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AboutusListModel(
    @SerializedName("AboutusList")
    val aboutusList: ArrayList<Aboutus>
){
    @Keep
    data class Aboutus(
        @SerializedName("ABT_FAQ_CREATEDBY")
        val aBTFAQCREATEDBY: Int,
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