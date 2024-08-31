package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StaffListModel(
    @SerializedName("StaffList")
    val staffList: List<Staff>
){
    @Keep
    data class Staff(
        @SerializedName("STAFF_CODE")
        val sTAFFCODE: String,
        @SerializedName("STAFF_FNAME")
        val sTAFFFNAME: String,
        @SerializedName("STAFF_GENDER")
        val sTAFFGENDER: Int,
        @SerializedName("STAFF_ID")
        val sTAFFID: Int,
        @SerializedName("STAFF_IMAGE")
        val sTAFFIMAGE: String,
        @SerializedName("STAFF_JOB_ROLE")
        val sTAFFJOBROLE: String,
        @SerializedName("STAFF_PHONE_NUMBER")
        val sTAFFPHONENUMBER: String,
        @SerializedName("STAFF_STATUS")
        val sTAFFSTATUS: Int,

        var isChecked : Boolean = false
    )
}