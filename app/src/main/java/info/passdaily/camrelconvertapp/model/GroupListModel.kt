package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GroupListModel(
    @SerializedName("GroupList")
    val groupList: List<Group>
){
    @Keep
    data class Group(
        @SerializedName("CREATED_BY")
        val cREATEDBY: String?,
        @SerializedName("GROUP_CREATED_DATE")
        val gROUPCREATEDDATE: String?,
        @SerializedName("GROUP_ID")
        val gROUPID: Int,
        @SerializedName("GROUP_NAME")
        val gROUPNAME: String?,
        @SerializedName("GROUP_STATUS")
        val gROUPSTATUS: Int,
        @SerializedName("GROUP_TYPE")
        val gROUPTYPE: Int
    )
}