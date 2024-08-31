package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class LoginStaffModel(
    @SerializedName("ADMIN_ID")
    val aDMINID: Int,
    @SerializedName("ADMIN_ROLE")
    val aDMINROLE: Int,
    @SerializedName("ADMIN_ROLE_NAME")
    val aDMINROLENAME: String,
    @SerializedName("LOGIN_ROLE")
    val lOGINROLE: String,
    @SerializedName("LOGIN_STATUS")
    val lOGINSTATUS: String,
    @SerializedName("STAFF_ID")
    val sTAFFID: Int,
    @SerializedName("SCHOOL_ID")
    val sCHOOL_ID: Int,
    @SerializedName("STAFF_FNAME")
    val sTAFFFNAME: String,
    @SerializedName("STAFF_IMAGE")
    val sTAFFIMAGE: String,
    //"STAFF_FNAME": "ANVER S",
    //"STAFF_IMAGE": "A9492EE5D4ED8EBB44CC06975A9F15D2.jpeg"

)