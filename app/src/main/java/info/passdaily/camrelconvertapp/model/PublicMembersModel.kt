package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PublicMembersModel(
    @SerializedName("PublicMembers")
    val publicMembers: List<PublicMember>
){
    @Keep
    data class PublicMember(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("GMEMBER_ID")
        val gMEMBERID: Int,
        @SerializedName("GMEMBER_NAME")
        val gMEMBERNAME: String,
        @SerializedName("GMEMBER_NUMBER")
        val gMEMBERNUMBER: String,
        @SerializedName("GROUP_ID")
        val gROUPID: Any,
        @SerializedName("GROUP_NAME")
        val gROUPNAME: String,

        var isChecked : Boolean = false
    )
}