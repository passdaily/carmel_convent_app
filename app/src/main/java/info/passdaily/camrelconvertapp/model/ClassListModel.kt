package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ClassListModel(
    @SerializedName("ClassList")
    val classList: List<Class>
) {
    @Keep
    data class Class(
        @SerializedName("CLASS_DESCRIPTION")
        val cLASSDESCRIPTION: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CLASS_STATUS")
        val cLASSSTATUS: Int
    )
}